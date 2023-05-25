package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.AddProductToCartDTO;
import com.abranlezama.ecommercestore.cart.dto.UpdateCartItemDTO;
import com.abranlezama.ecommercestore.exception.ConflictException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.product.Product;
import com.abranlezama.ecommercestore.product.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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
    @Captor
    private ArgumentCaptor<Cart> cartArgumentCaptor;
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
    @DisplayName("throw NotFoundException when customer cart is not found")
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
    @DisplayName("throw NotFoundException when adding product to cart that does not exist")
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
    @DisplayName("throw NotFoundException when adding product to a cart that does not exist")
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

    @Nested
    @DisplayName("update customer cart item")
    class UpdateCustomerCartItem {
        @Test
        @DisplayName("update item")
        void updateCustomerCartItemWhenItIsInCart() {
            // Given
            UpdateCartItemDTO updateDto = new UpdateCartItemDTO(1L, (short) 5);
            String customerEmail = "duke.last@gmail.com";

            // generate cart and cart items
            Product product = ProductMother.complete().build();
            CartItem cartItem = CartItem.builder()
                    .quantity((short) 2)
                    .product(product)
                    .build();
            Cart cart = Cart.builder()
                    .cartItems(Set.of(cartItem))
                    .totalCost(cartItem.getQuantity() * cartItem.getProduct().getPrice())
                    .build();

            given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.of(cart));

            // When
            cut.updateCartItem(updateDto, customerEmail);

            // Then
            then(cartRepository).should().save(cartArgumentCaptor.capture());
            Cart savedCart = cartArgumentCaptor.getValue();
            List<CartItem> savedCartItem = savedCart.getCartItems().stream().toList();

            assertThat(savedCartItem.get(0).getProduct().getId()).isEqualTo(product.getId());
            assertThat(savedCart.getTotalCost()).isEqualTo(5 * product.getPrice());
            assertThat(savedCartItem.get(0).getQuantity()).isEqualTo((short) 5);
        }

        @Test
        @DisplayName("fail item update when inventory is less than wanted quantity")
        void throwConflictExceptionWhenWantedQuantityIsLessThanAvailableInventory() {
            // Given
            UpdateCartItemDTO updateDto = new UpdateCartItemDTO(1L, (short) 5);
            String customerEmail = "duke.last@gmail.com";

            // generate cart and cart items
            Product product = ProductMother.complete().stockQuantity((short) 1).build();
            CartItem cartItem = CartItem.builder()
                    .quantity((short) 2)
                    .product(product)
                    .build();
            Cart cart = Cart.builder()
                    .cartItems(Set.of(cartItem))
                    .totalCost(cartItem.getQuantity() * cartItem.getProduct().getPrice())
                    .build();

            given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.of(cart));

            // When
            assertThatThrownBy(() -> cut.updateCartItem(updateDto, customerEmail))
                    .hasMessage(ExceptionMessages.PRODUCT_OUT_OF_STOCK)
                    .isInstanceOf(ConflictException.class);

        }

        @Test
        @DisplayName("throw NotFoundException when customer cart is not found")
        void throwNotFoundExceptionWhenUpdatingCartItemAndCartIsNotFound() {
            // Given
            UpdateCartItemDTO updateDto = new UpdateCartItemDTO(1L, (short) 5);
            String customerEmail = "duke.last@gmail.com";

            given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> cut.updateCartItem(updateDto, customerEmail))
                    .hasMessage(ExceptionMessages.CART_NOT_FOUND)
                    .isInstanceOf(NotFoundException.class);

            // Then
            then(cartRepository).should(never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("throw NotFoundException when item not found in customer cart")
        void throwCartItemNotFoundWhenUpdatingCartItemThatIsNotICustomerCart() {
            // Given
            UpdateCartItemDTO updateDto = new UpdateCartItemDTO(1L, (short) 5);
            String customerEmail = "duke.last@gmail.com";
            Cart cart = Cart.builder().cartItems(new HashSet<>()).build();

            given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.of(cart));

            // When
            assertThatThrownBy(() -> cut.updateCartItem(updateDto, customerEmail))
                    .hasMessage(ExceptionMessages.CART_ITEM_NOT_FOUND)
                    .isInstanceOf(NotFoundException.class);

            // Then
            then(cartRepository).should(never()).save(any(Cart.class));
        }


    }
}
