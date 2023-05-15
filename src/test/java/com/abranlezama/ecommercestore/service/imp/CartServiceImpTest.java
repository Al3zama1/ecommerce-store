package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.cart.AddItemToCartDto;
import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.dto.cart.mapper.CartMapper;
import com.abranlezama.ecommercestore.exception.CustomerNotFound;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.objectmother.UserMother;
import com.abranlezama.ecommercestore.repository.CartItemRepository;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.ProductRepository;
import com.abranlezama.ecommercestore.service.CartService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class CartServiceImpTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartMapper cartMapper;
    @Captor
    ArgumentCaptor<CartItem> cartItemArgumentCaptor;
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

    // Tests for adding product to customers cart

    @Test
    void shouldAddProductToCustomerShoppingCart() {
        // Give
        Cart cart = Cart.builder().cartItems(Set.of()).build();
        Customer customer = CustomerMother.complete().cart(cart).build();
        Product product = ProductMother.complete().id(1L).build();
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;
        int quantity = 3;

        given(customerRepository.findByUser_Email(userEmail)).willReturn(Optional.of(customer));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // When
        cut.addProductToCart(userEmail, productId, quantity);

        // Then
        then(cartItemRepository).should().save(cartItemArgumentCaptor.capture());
        assertThat(cartItemArgumentCaptor.getValue().getProduct()).isEqualTo(product);
        assertThat(cartItemArgumentCaptor.getValue().getCart()).isEqualTo(cart);
        assertThat(cartItemArgumentCaptor.getValue().getQuantity()).isEqualTo(quantity);
    }

    @Test
    void shouldIncrementCartItemCountWhenItIsAlreadyInTheCart() {
        // Give
        Product product = ProductMother.complete().id(1L).build();
        Cart cart = Cart.builder().build();
        CartItem cartItem = new CartItem(product, cart, 1);
        cart.setCartItems(Set.of(cartItem));

        Customer customer = CustomerMother.complete().cart(cart).build();

        String userEmail = "duke.last@gmail.com";
        long productId = 1L;
        int quantity = 3;

        given(customerRepository.findByUser_Email(userEmail)).willReturn(Optional.of(customer));

        // When
        cut.addProductToCart(userEmail, productId, quantity);

        // Then
        then(cartItemRepository).should().save(cartItemArgumentCaptor.capture());
        assertThat(cartItemArgumentCaptor.getValue().getProduct()).isEqualTo(product);
        assertThat(cartItemArgumentCaptor.getValue().getQuantity()).isEqualTo(4);
    }

}
