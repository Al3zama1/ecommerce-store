package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.product.AddProductDTO;
import com.abranlezama.ecommercestore.dto.product.ProductDTO;
import com.abranlezama.ecommercestore.dto.product.UpdateProductRequestDTO;
import com.abranlezama.ecommercestore.dto.product.mapper.ProductMapper;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.ProductNotFoundException;
import com.abranlezama.ecommercestore.model.Category;
import com.abranlezama.ecommercestore.model.CategoryType;
import com.abranlezama.ecommercestore.model.Product;
import com.abranlezama.ecommercestore.repository.CategoryRepository;
import com.abranlezama.ecommercestore.repository.ProductCategoryRepository;
import com.abranlezama.ecommercestore.repository.ProductRepository;
import com.abranlezama.ecommercestore.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CategoryRepository categoryRepository;
    private final ProductCategoryRepository productCategoryRepository;

    @Override
    public List<ProductDTO> getProducts(int page, int pageSize, Set<String> categories) {
        PageRequest pageRequest = PageRequest.of(page, pageSize);
        if (categories.size() == 0) {
            return productRepository.findAll(pageRequest)
                    .stream()
                    .map(productMapper::mapProductToDTO)
                    .toList();
        }

        Set<CategoryType> categoryTypes = getCategoryTypes(categories);

        return productRepository.findProductByCategory(pageRequest, categoryTypes)
                .stream()
                .map(productMapper::mapProductToDTO)
                .toList();
    }

    @Override
    public Long createProduct(String userEmail, AddProductDTO requestDto) {
        // convert dto to product
        Product product = productMapper.mapAddProductRequestToEntity(requestDto);

        // fetch categories
        Set<CategoryType> categoryTypes = getCategoryTypes(requestDto.categories());

        Set<Category> categories = categoryRepository.findAllByCategoryIn(categoryTypes);

        product.setProductCategories(categories);
        product = productRepository.save(product);
        return product.getId();
    }

    @Override
    public void removeProduct(String userEmail, Long productId) {
        // fetch product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND));

        productRepository.delete(product);
    }

    @Override
    public ProductDTO updateProduct(String userEmail, long productId, UpdateProductRequestDTO requestDto) {
        // verify existence of product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND));

        Set<CategoryType> categoryTypes = getCategoryTypes(requestDto.categories());
        Set<Category> categories = categoryRepository.findAllByCategoryIn(categoryTypes);

        // update product fields, except key
        product.setName(requestDto.name());
        product.setDescription(requestDto.description());
        product.setPrice(requestDto.price());
        product.setStockQuantity(requestDto.stockQuantity());
        product.setProductCategories(categories);

        // save product with updated fields
        product = productRepository.save(product);

        return productMapper.mapProductToDTO(product);
    }

    private Set<CategoryType> getCategoryTypes(Set<String> categories) {
        return Arrays.stream(CategoryType.values())
                .filter(type -> categories.contains(type.name().toLowerCase()))
                .collect(Collectors.toSet());
    }

}
