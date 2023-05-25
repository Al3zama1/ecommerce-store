package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
@DisplayName("customer service cart")
class CustomerCartServiceImpTest {

    @Mock
    private CartRepository cartRepository;
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
}
