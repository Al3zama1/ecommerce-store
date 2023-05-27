package com.abranlezama.ecommercestore.product.service.imp;

import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import com.abranlezama.ecommercestore.product.model.Product;
import com.abranlezama.ecommercestore.product.repository.ProductRepository;
import com.abranlezama.ecommercestore.product.dto.ProductDTO;
import com.abranlezama.ecommercestore.product.mapper.ProductMapper;
import com.abranlezama.ecommercestore.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImp implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    @Override
    public List<ProductDTO> retrieveProducts(int page, int pageSize) {
        return productRepository.findAllByOrderByStockQuantityDesc(PageRequest.of(page, pageSize))
                .stream()
                .map(productMapper::mapProductToDto)
                .toList();
    }

    @Override
    public ProductDTO retrieveProduct(long productId) {
        Product product =  productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND));

        return productMapper.mapProductToDto(product);
    }
}
