package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.AddProductToCartDTO;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.product.Product;
import com.abranlezama.ecommercestore.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
@DisplayName("customer service cart")
class CustomerCartServiceImpTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartMapper cartMapper;
    @InjectMocks
    private CustomerCartServiceImp cut;

    @Test
    @DisplayName("retrieve customer cart")
    void returnCustomerCart() {
        // Given
        String customerEmail = "duke.last@gmail.com";
        Cart cart = Cart.builder().build();

        given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.of(cart));

        // When
        this.cut.retrieveCustomerCart(customerEmail);

        // Then
        then(cartMapper).should().mapCartToDto(cart);
    }

    @Test
    @DisplayName("throw cart not found when customer cart is not found")
    void throwNotFoundExceptionWhenCustomerCartIsNotFound() {
        // Given
        String customerEmail = "duke.last@gmail.com";

        given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> this.cut.retrieveCustomerCart(customerEmail))
                .hasMessage(ExceptionMessages.CART_NOT_FOUND)
                .isInstanceOf(NotFoundException.class);

        // Then
        then(cartMapper).shouldHaveNoInteractions();
    }

    @Test
    @DisplayName("add product to customer cart")
    void addProductToCustomerCart() {
        // Given
        AddProductToCartDTO addDto = new AddProductToCartDTO(1L, (short) 4);
        String customerEmail = "duke.last@gmail.com";
        Product product = ProductMother.complete().build();
        Cart cart = Cart.builder().cartItems(new HashSet<>()).build();

        given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.of(cart));
        given(productRepository.findById(addDto.productId())).willReturn(Optional.of(product));

        // When
        this.cut.addProductToCart(addDto, customerEmail);

        // Then
        then(cartRepository).should().save(cart);
    }

    @Test
    @DisplayName("throw product not found when adding product to cart that does not exist")
    void throwProductNotFoundWhenAddingProductToCartThatDoesNotExist() {
        // Given
        AddProductToCartDTO addDto = new AddProductToCartDTO(1L, (short) 4);
        String customerEmail = "duke.last@gmail.com";

        given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.of(Cart.builder().build()));
        given(productRepository.findById(addDto.productId())).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> this.cut.addProductToCart(addDto, customerEmail))
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND)
                .isInstanceOf(NotFoundException.class);

        // Then
        then(cartRepository).should(never()).save(any(Cart.class));
    }

    @Test
    @DisplayName("throw cart not found adding product to a cart that does not exist")
    void throwCartNotFoundWhenAddingProductToCartThatDoesNotExist() {
        // Given
        AddProductToCartDTO addDto = new AddProductToCartDTO(1L, (short) 4);
        String customerEmail = "duke.last@gmail.com";

        given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.addProductToCart(addDto, customerEmail))
                .hasMessage(ExceptionMessages.CART_NOT_FOUND)
                .isInstanceOf(NotFoundException.class);

        // Then
        then(cartRepository).should(never()).save(any(Cart.class));
    }
}
