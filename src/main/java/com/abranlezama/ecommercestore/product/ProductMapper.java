package com.abranlezama.ecommercestore.product;

import com.abranlezama.ecommercestore.product.dto.ProductDTO;
import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {

    ProductDTO mapProductToDto(Product product);
}
