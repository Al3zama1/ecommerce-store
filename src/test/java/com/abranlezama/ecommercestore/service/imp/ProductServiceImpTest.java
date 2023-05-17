package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.product.AddProductRequestDTO;
import com.abranlezama.ecommercestore.dto.product.mapper.ProductMapper;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.ProductNotFoundException;
import com.abranlezama.ecommercestore.model.CategoryType;
import com.abranlezama.ecommercestore.model.Product;
import com.abranlezama.ecommercestore.objectmother.AddProductRequestDTOMother;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.repository.CategoryRepository;
import com.abranlezama.ecommercestore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;
    @Mock
    private CategoryRepository categoryRepository;
    @Captor
    private ArgumentCaptor<Product> productArgumentCaptor;

    @InjectMocks
    private ProductServiceImp cut;


    @Test
    void shouldReturnProductsPage() {
        // Given
        int page = 0;
        int pageSize = 20;
        Set<String> categories = Set.of();
        Pageable pageable = PageRequest.of(page, pageSize);

        given(productRepository.findAll(pageable)).willReturn(Page.empty());


        // When
        cut.getProducts(page, pageSize, categories);

        // Then
        then(productRepository).should(never()).findProductByCategory(any(), any());
    }

    @Test
    void shouldReturnProductPageBasedOnCategoriesProvided() {
        // Given
        int page = 0;
        int pageSize = 20;
        Set<String> categories = Set.of("electronics");
        Pageable pageable = PageRequest.of(page, pageSize);

        given(productRepository.findProductByCategory(pageable, Set.of(CategoryType.ELECTRONICS)))
                .willReturn(Page.empty());

        // When
        cut.getProducts(page, pageSize, categories);

        // Then
        then(productRepository).should(never()).findAll(pageable);
    }

    // test creation of product
    @Test
    void shouldCreateNewProduct() {
        // Given
        String userEmail = "duke.last@gmail.com";
        Set<String> productCategories = Set.of("electronics", "education");
        Product product = ProductMother.complete().build();
        AddProductRequestDTO requestDto = AddProductRequestDTOMother.create()
                .categories(productCategories)
                .build();

        given(productMapper.mapAddProductRequestToEntity(requestDto))
                .willReturn(product);
        given(productRepository.save(product)).willAnswer(invocation -> {
            Product temp = invocation.getArgument(0);
            temp.setId(1L);
            return temp;
        });

        // When
        cut.createProduct(userEmail, requestDto);

        // Then
        then(productRepository).should().save(product);
    }

    // test removal of product
    @Test
    void shouldRemoveProduct() {
        // Given
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;
        Product product = ProductMother.complete().build();

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // When
        cut.removeProduct(userEmail, productId);

        // Then
        then(productRepository).should().delete(product);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenDeletingNonExistingProduct() {
        // Given
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;

        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.removeProduct(userEmail, productId))
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND)
                .isInstanceOf(ProductNotFoundException.class);

        // Then
        then(productRepository).should(never()).delete(any());
    }

}
