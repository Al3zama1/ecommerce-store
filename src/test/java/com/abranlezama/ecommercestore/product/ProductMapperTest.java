package com.abranlezama.ecommercestore.product;

import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.product.dto.ProductDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ProductMapperTest {

    ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    @DisplayName("convert product to DTO")
    void shouldConvertProductToDto() {
        // Given
        Product product = ProductMother.complete().build();

        // When
        ProductDTO productDto = mapper.mapProductToDto(product);

        // Then
        assertThat(productDto.id()).isEqualTo(product.getId());
        assertThat(productDto.name()).isEqualTo(product.getName());
        assertThat(productDto.description()).isEqualTo(product.getDescription());
        assertThat(productDto.price()).isEqualTo(product.getPrice());
    }

}
