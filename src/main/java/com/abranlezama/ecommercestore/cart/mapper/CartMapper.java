package com.abranlezama.ecommercestore.cart.mapper;

import com.abranlezama.ecommercestore.cart.Cart;
import com.abranlezama.ecommercestore.cart.CartItem;
import com.abranlezama.ecommercestore.cart.dto.CartDTO;
import com.abranlezama.ecommercestore.cart.dto.CartItemDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CartMapper {

    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "productId", source = "product.id")
    CartItemDTO mapCartItemToDto(CartItem cartItem);

    CartDTO mapCartToDto(Cart cart);
}
