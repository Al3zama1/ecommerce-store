package com.abranlezama.ecommercestore.product;

import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("product service")
class ProductServiceImpTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @InjectMocks
    private ProductServiceImp cut;

    @Test
    @DisplayName("return products")
    void returnProducts() {
        // Given
        int page = 0;
        int pageSize = 20;
        Product product = ProductMother.complete().build();
        Page<Product> productPage = new PageImpl<>(List.of(product));

        given(productRepository.findAllByOrderByStockQuantityDesc(PageRequest.of(page, pageSize))).willReturn(productPage);

        // When
        this.cut.retrieveProducts(page, pageSize);

        // Then
        then(productMapper).should().mapProductToDto(product);
    }

    @Test
    @DisplayName("return product associated with ID")
    void returnProductWhenItExists() {
        // Given
        long productId = 1L;
        Product product = ProductMother.complete().build();;

        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // When
        this.cut.retrieveProduct(productId);

        // Then
        then(productMapper).should().mapProductToDto(product);
    }

    @Test
    @DisplayName("throw NotFoundException when retrieving product that does not exist")
    void throwNotFoundExceptionWhenGivenIdIsNotAssociatedToAProduct() {
        // Given
        long productId = 1L;

        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> this.cut.retrieveProduct(productId))
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND)
                .isInstanceOf(NotFoundException.class);

        // Then
        then(productMapper).shouldHaveNoInteractions();
    }

}
