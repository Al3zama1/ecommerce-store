package com.abranlezama.ecommercestore.dto.product.mapper;

import com.abranlezama.ecommercestore.dto.product.AddProductDTO;
import com.abranlezama.ecommercestore.dto.product.ProductDTO;
import com.abranlezama.ecommercestore.model.Product;
import com.abranlezama.ecommercestore.objectmother.AddProductDTOMother;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void shouldConvertProductEntityToDTO() {
        // Given
        Product product = ProductMother.complete().build();

        // When
        ProductDTO dtoResponse = mapper.mapProductToDTO(product);

        // Then
        assertThat(dtoResponse.id()).isEqualTo(product.getId());
        assertThat(dtoResponse.name()).isEqualTo(product.getName());
        assertThat(dtoResponse.description()).isEqualTo(product.getDescription());
        assertThat(dtoResponse.price()).isEqualTo(product.getPrice());
    }

    @Test
    void shouldReturnNullWhenGivenProductIsNull() {
        // Given
        Product product = null;

        // When
        ProductDTO dtoResponse = mapper.mapProductToDTO(product);

        // Then
        assertThat(dtoResponse).isNull();
    }

    @Test
    void shouldConvertRequestToAddProductToProductEntity() {
        // Given
        AddProductDTO requestDto = AddProductDTOMother.create().build();

        // When
        Product product = mapper.mapAddProductRequestToEntity(requestDto);

        // Then
        assertThat(product.getName()).isEqualTo(requestDto.name());
        assertThat(product.getDescription()).isEqualTo(requestDto.description());
        assertThat(product.getPrice()).isEqualTo(requestDto.price());
        assertThat(product.getStockQuantity()).isEqualTo(requestDto.stockQuantity());
    }

}
