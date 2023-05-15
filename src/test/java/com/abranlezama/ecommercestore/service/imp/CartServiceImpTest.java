package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.dto.cart.mapper.CartMapper;
import com.abranlezama.ecommercestore.exception.CustomerNotFound;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.model.Cart;
import com.abranlezama.ecommercestore.model.CartItem;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.model.User;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.objectmother.UserMother;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CartServiceImpTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CartMapper cartMapper;
    @InjectMocks
    private CartServiceImp cut;

    @Test
    void shouldReturnCustomerCart() {
        // Given
        User user = UserMother.complete().build();
        Cart cart = Cart.builder().cartItems(Set.of()).build();
        Customer customer = CustomerMother.complete()
                .cart(cart)
                .build();


        given(customerRepository.findByUser_Email(user.getEmail())).willReturn(Optional.of(customer));
        given(cartMapper.mapCartToDto(cart)).willReturn(new CartDTO());

        // When
        cut.getCustomerCart(user.getEmail());

        // Then
        then(cartMapper).should().mapCartToDto(customer.getCart());
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenUserIsNotACustomer() {
        // Given
        User user = UserMother.complete().build();

        given(customerRepository.findByUser_Email(user.getEmail())).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.getCustomerCart(user.getEmail()))
                .hasMessage(ExceptionMessages.CUSTOMER_NOT_FOUND)
                .isInstanceOf(CustomerNotFound.class);

        // Then
        then(cartMapper).shouldHaveNoInteractions();
    }

}
