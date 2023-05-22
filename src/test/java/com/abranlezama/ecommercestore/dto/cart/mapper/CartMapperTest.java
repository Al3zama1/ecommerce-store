package com.abranlezama.ecommercestore.dto.cart.mapper;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.dto.cart.CartItemDTO;
import com.abranlezama.ecommercestore.model.Cart;
import com.abranlezama.ecommercestore.model.CartItem;
import com.abranlezama.ecommercestore.model.Product;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class CartMapperTest {

    CartMapper mapper = Mappers.getMapper(CartMapper.class);

    @Test
    void shouldMapCartItemToCartItemDto() {
        // Given
        Product product = ProductMother.complete().build();
        CartItem cartItem = CartItem.builder()
                .product(product)
                .quantity((short) 2)
                .build();

        // When
        CartItemDTO cartItemDto = mapper.mapCartItemToDto(cartItem);

        // Then
        assertThat(cartItemDto.name()).isEqualTo(cartItem.getProduct().getName());
        assertThat(cartItemDto.quantity()).isEqualTo(cartItem.getQuantity());
        assertThat(cartItemDto.price()).isEqualTo(cartItem.getProduct().getPrice());
    }

    @Test
    void shouldConvertCartToCartDto() {
        Product product = ProductMother.complete().build();
        CartItem cartItem = CartItem.builder()
                .product(product)
                .build();

        Cart cart = Cart.builder()
                .cartItems(Set.of(cartItem))
                .totalCost(product.getPrice())
                .build();

        // When
        CartDTO cartDto = mapper.mapCartToDto(cart);

        // Then
        assertThat(cartDto.cartTotal()).isEqualTo(cart.getTotalCost());
        assertThat(cartDto.cartItems().size()).isEqualTo(cart.getCartItems().size());
    }

}
