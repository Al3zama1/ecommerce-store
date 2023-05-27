package com.abranlezama.ecommercestore.cart.mapper;

import com.abranlezama.ecommercestore.cart.model.Cart;
import com.abranlezama.ecommercestore.cart.model.CartItem;
import com.abranlezama.ecommercestore.cart.dto.CartDTO;
import com.abranlezama.ecommercestore.cart.dto.CartItemDTO;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.product.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("cart mapper")
class CartMapperTest {

    CartMapper mapper = Mappers.getMapper(CartMapper.class);

    @Test
    @DisplayName("convert cart item to cart item dto")
    void convertCartItemToCartItemDto() {
        // Given
        Product product = ProductMother.complete().build();
        CartItem cartItem = CartItem.builder()
                .quantity((short) 3)
                .product(product)
                .build();

        // When
        CartItemDTO cartItemDto = mapper.mapCartItemToDto(cartItem);

        // Then
        assertThat(cartItemDto.productId()).isEqualTo(cartItem.getProduct().getId());
        assertThat(cartItemDto.name()).isEqualTo(cartItem.getProduct().getName());
        assertThat(cartItemDto.price()).isEqualTo(cartItem.getProduct().getPrice());
        assertThat(cartItemDto.quantity()).isEqualTo(cartItem.getQuantity());
    }

    @Test
    @DisplayName("convert cart to cart dto")
    void convertCartToCartDto() {
        // Given
        Product product = ProductMother.complete().build();
        CartItem cartItem = CartItem.builder()
                .quantity((short) 3)
                .product(product)
                .build();
        Cart cart = Cart.builder()
                .totalCost(product.getPrice() * cartItem.getQuantity())
                .cartItems(Set.of(cartItem))
                .build();

        // When
        CartDTO cartDto = mapper.mapCartToDto(cart);

        // Then
        assertThat(cartDto.totalCost()).isEqualTo(cart.getTotalCost());
        assertThat(cartDto.cartItems().size()).isEqualTo(cart.getCartItems().size());
    }
}
