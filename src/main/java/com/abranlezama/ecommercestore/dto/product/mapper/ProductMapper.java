package com.abranlezama.ecommercestore.dto.product.mapper;

import com.abranlezama.ecommercestore.dto.product.AddProductRequestDTO;
import com.abranlezama.ecommercestore.dto.product.ProductResponseDTO;
import com.abranlezama.ecommercestore.model.Product;
import org.mapstruct.Mapper;

@Mapper
public interface ProductMapper {

    ProductResponseDTO mapProductToDTO(Product product);

    Product mapAddProductRequestToEntity(AddProductRequestDTO requestDto);
}
