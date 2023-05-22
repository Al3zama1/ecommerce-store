package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.dto.cart.mapper.CartMapper;
import com.abranlezama.ecommercestore.exception.UserNotFound;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.ProductNotFoundException;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.repository.CartItemRepository;
import com.abranlezama.ecommercestore.repository.CartRepository;
import com.abranlezama.ecommercestore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CartServiceImpTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CartMapper cartMapper;
    @Captor
    ArgumentCaptor<Cart> cartArgumentCaptor;
    @InjectMocks
    private CartServiceImp cut;

    @Test
    void shouldReturnCustomerCart() {
        // Given
        String userEmail = "duke.last@gmail.com";
        Cart cart = Cart.builder().cartItems(Set.of()).build();

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.of(cart));
        given(cartMapper.mapCartToDto(cart)).willReturn(CartDTO.builder().build());

        // When
        cut.getCustomerCart(userEmail);

        // Then
        then(cartMapper).should().mapCartToDto(cart);
    }

    @Test
    void shouldThrowCustomerNotFoundExceptionWhenUserIsNotACustomer() {
        // Given
        String userEmail = "duke.last@gmail.com";

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.getCustomerCart(userEmail))
                .hasMessage(ExceptionMessages.USER_NOT_FOUND)
                .isInstanceOf(UserNotFound.class);

        // Then
        then(cartMapper).shouldHaveNoInteractions();
    }

    // Tests for adding product to customers cart
    @Test
    void shouldAddProductToCustomerShoppingCart() {
        // Give
        Cart cart = Cart.builder().cartItems(new HashSet<>()).build();
        Product product = ProductMother.complete().build();
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;
        short quantity = 3;

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.of(cart));
        given(productRepository.findById(productId)).willReturn(Optional.of(product));

        // When
        cut.addProductToCart(userEmail, productId, quantity);

        // Then
        then(cartRepository).should().save(cartArgumentCaptor.capture());
        Cart savedCart = cartArgumentCaptor.getValue();
        assertThat(savedCart.getCartItems().size()).isEqualTo(1);
        assertThat(cart.getTotalCost()).isEqualTo(product.getPrice() * quantity);
    }

    @Test
    void shouldIncrementCartItemCountWhenItIsAlreadyInTheCart() {
        // Give
        Product product = ProductMother.complete().id(1L).build();
        Cart cart = Cart.builder().build();
        CartItem cartItem = new CartItem(product, cart, (short) 1);
        cart.setCartItems(Set.of(cartItem));

        String userEmail = "duke.last@gmail.com";
        long productId = 1L;
        short quantity = 3;

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.of(cart));

        // When
        cut.addProductToCart(userEmail, productId, quantity);

        // Then
        then(cartRepository).should().save(cartArgumentCaptor.capture());

        Cart savedCart = cartArgumentCaptor.getValue();
        CartItem savedCartItem = savedCart.getCartItems().stream().findFirst().orElseThrow();

        assertThat(savedCartItem.getProduct()).isEqualTo(product);
        assertThat(savedCartItem.getQuantity()).isEqualTo((short) 4);
        assertThat(savedCart.getTotalCost()).isEqualTo(4 * product.getPrice());
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenAddingProductToCartThatDoesNotExist() {
        // Give
        Cart cart = Cart.builder().cartItems(Set.of()).build();
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;
        short quantity = 3;

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.of(cart));
        given(productRepository.findById(productId)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.addProductToCart(userEmail, productId, quantity))
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND)
                .isInstanceOf(ProductNotFoundException.class);

        // Then
        then(cartRepository).should(never()).save(any());
    }

    // tests to update cart product
    @Test
    void shouldUpdateCustomerCartProduct() {
        // Given
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;
        short quantity = 3;

        Product product = ProductMother.complete().build();
        Cart cart = Cart.builder().totalCost(product.getPrice()).build();
        CartItem cartItem = CartItem.builder().cart(cart).product(product).quantity((short) 3).build();
        cart.setCartItems(Set.of(cartItem));

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.of(cart));

        // When
        cut.updateCartProduct(userEmail, productId, quantity);

        // Then
        then(cartRepository).should().save(cartArgumentCaptor.capture());
        Cart savedCart = cartArgumentCaptor.getValue();

        assertThat(savedCart.getTotalCost()).isEqualTo(3 * product.getPrice());
        assertThat(savedCart.getCartItems().size()).isEqualTo(1);
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenUpdatingProductNotInCustomerCart() {
        // Given
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;
        short quantity = 3;

        Cart cart = Cart.builder().cartItems(Set.of()).build();

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.of(cart));

        // When
        assertThatThrownBy(() -> cut.updateCartProduct(userEmail, productId, quantity))
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND)
                .isInstanceOf(ProductNotFoundException.class);

        // Then
        then(cartRepository).should(never()).save(any());
    }

    // test removal of product from customer's shopping cart

    @Test
    void shouldRemoveProductFromCustomerShoppingCart() {
        // Given
        Product product = ProductMother.complete().id(1L).build();
        Cart cart = Cart.builder().build();
        CartItem cartItem = CartItem.builder().product(product).cart(cart).build();
        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);


        String userEmail = "duke.last@gmail.com";
        long productId = 1L;

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.of(cart));

        // When
        cut.removeCartProduct(userEmail, productId);

        // Then
        then(cartRepository).should().save(cartArgumentCaptor.capture());
        then(cartItemRepository).should().delete(cartItem);
        Cart savedCart = cartArgumentCaptor.getValue();

        assertThat(savedCart.getCartItems().size()).isEqualTo(0);
        assertThat(savedCart.getTotalCost()).isEqualTo(0);
    }

    @Test
    void shouldThrowCustomerNotFoundWhenUserIsNotCustomer() {
        // Given
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.empty());

        // When
        assertThatThrownBy(() -> cut.removeCartProduct(userEmail, productId))
                .hasMessage(ExceptionMessages.USER_NOT_FOUND)
                .hasMessage(ExceptionMessages.USER_NOT_FOUND);

        // Then
        then(cartRepository).should(never()).save(any());
    }

    @Test
    void shouldThrowProductNotFoundExceptionWhenRemovingProductNotInCart() {
        // Given
        Cart cart = Cart.builder().cartItems(Set.of()).build();
        String userEmail = "duke.last@gmail.com";
        long productId = 1L;

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.of(cart));

        // When
        assertThatThrownBy(() -> cut.removeCartProduct(userEmail, productId))
                .hasMessage(ExceptionMessages.PRODUCT_NOT_FOUND)
                .isInstanceOf(ProductNotFoundException.class);

        // Then
        then(cartRepository).should(never()).save(any());
    }

}
