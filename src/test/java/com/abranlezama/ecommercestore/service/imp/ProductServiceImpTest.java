package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.product.mapper.ProductMapper;
import com.abranlezama.ecommercestore.model.CategoryType;
import com.abranlezama.ecommercestore.model.Product;
import com.abranlezama.ecommercestore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

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

    @InjectMocks
    private ProductServiceImp cut;


    @Test
    void shouldReturnProductsPage() {
        // Given
        int page = 0;
        int pageSize = 20;
        List<String> categories = List.of();
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
        List<String> categories = List.of("technology");
        Pageable pageable = PageRequest.of(page, pageSize);

        given(productRepository.findProductByCategory(pageable, List.of(CategoryType.TECHNOLOGY)))
                .willReturn(Page.empty());

        // When
        cut.getProducts(page, pageSize, categories);

        // Then
        then(productRepository).should(never()).findAll(pageable);
    }

}
