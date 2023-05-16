package com.abranlezama.ecommercestore.dto.cart.mapper;

import com.abranlezama.ecommercestore.dto.cart.CartItemDTO;
import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.model.Cart;
import com.abranlezama.ecommercestore.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CartMapper {

    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "price", source = "product.price")
    @Mapping(target = "quantity", source = "quantity")
    @Mapping(target = "productId", source = "product.id")
    CartItemDTO mapCartItemToDto(CartItem cartItem);


    @Mapping(target = "cartTotal", source = "totalCost")
    @Mapping(target = "cartItems", source = "cartItems")
    CartDTO mapCartToDto(Cart cart);
}
