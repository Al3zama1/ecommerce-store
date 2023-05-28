package com.abranlezama.ecommercestore.order.service.imp;

import com.abranlezama.ecommercestore.cart.model.Cart;
import com.abranlezama.ecommercestore.cart.model.CartItem;
import com.abranlezama.ecommercestore.cart.repository.CartItemRepository;
import com.abranlezama.ecommercestore.cart.repository.CartRepository;
import com.abranlezama.ecommercestore.customer.model.Customer;
import com.abranlezama.ecommercestore.exception.BadRequestException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import com.abranlezama.ecommercestore.objectmother.CustomerMother;
import com.abranlezama.ecommercestore.objectmother.ProductMother;
import com.abranlezama.ecommercestore.order.model.Order;
import com.abranlezama.ecommercestore.order.model.OrderStatus;
import com.abranlezama.ecommercestore.order.repository.OrderRepository;
import com.abranlezama.ecommercestore.order.repository.OrderStatusRepository;
import com.abranlezama.ecommercestore.order.util.OrderStatusType;
import com.abranlezama.ecommercestore.product.model.Product;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CustomerOrderServiceImpTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private OrderStatusRepository orderStatusRepository;
    @Captor
    private ArgumentCaptor<Cart> cartArgumentCaptor;
    @Captor
    private ArgumentCaptor<Order> orderArgumentCaptor;
    @InjectMocks
    private CustomerOrderServiceImp cut;


    @Nested
    @DisplayName("customer order creation service")
    class CustomerOrderCreation {

        @Test
        @DisplayName("create customer order")
        void createCustomerOrder() {
            // Given
            String customerEmail = "duke.last@gmail.com";
            Product product = ProductMother.complete().build();
            Customer customer = CustomerMother.complete().build();
            OrderStatus orderStatus = OrderStatus.builder().build();
            CartItem cartItem = CartItem.builder()
                    .quantity((short)1)
                    .product(product)
                    .build();
            Cart cart = Cart.builder()
                    .customer(customer)
                    .cartItems(new HashSet<>())
                    .totalCost(cartItem.getQuantity() * cartItem.getProduct().getPrice())
                    .build();
            cart.getCartItems().add(cartItem);

            given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.of(cart));
            given(orderStatusRepository.findByStatus(OrderStatusType.PROCESSING)).willReturn(Optional.of(orderStatus));
            given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
                Order savedOrder = invocation.getArgument(0);
                savedOrder.setId(1L);
                return savedOrder;
            });

            // When
            cut.createCustomerOrder(customerEmail);

            // Then
            then(orderRepository).should().save(orderArgumentCaptor.capture());
            Order savedOrder = orderArgumentCaptor.getValue();
            assertThat(savedOrder.getTotalCost()).isEqualTo(cartItem.getProduct().getPrice() * cartItem.getQuantity());

            then(cartRepository).should().save(cartArgumentCaptor.capture());
            Cart savedCart = cartArgumentCaptor.getValue();
            assertThat(savedCart.getCartItems().size()).isEqualTo(0);
            assertThat(savedCart.getTotalCost()).isEqualTo(0);
        }

        @Test
        @DisplayName("throw NotFoundException when customer cart is not found")
        void throwNotFoundExceptionWhenCustomerCartNotFound() {
            // Given
            String customerEmail = "duke.last@gmail.com";

            given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> cut.createCustomerOrder(customerEmail))
                    .hasMessage(ExceptionMessages.CART_NOT_FOUND)
                    .isInstanceOf(NotFoundException.class);

            // Then
            then(orderRepository).shouldHaveNoInteractions();
            then(cartItemRepository).shouldHaveNoInteractions();
            then(cartRepository).should(never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("throw BadRequestException when attempting to create empty order")
        void throwBadRequestExceptionWhenAttemptToCreateEmptyOrder() {
            // Given
            String customerEmail = "duke.last@gmail.com";
            Cart cart = Cart.builder().cartItems(Set.of()).build();

            given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.of(cart));

            // When
            assertThatThrownBy(() -> cut.createCustomerOrder(customerEmail))
                    .hasMessage(ExceptionMessages.EMPTY_ORDER)
                    .isInstanceOf(BadRequestException.class);

            // Then
            then(orderRepository).shouldHaveNoInteractions();
            then(cartItemRepository).shouldHaveNoInteractions();
            then(cartRepository).should(never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("throw RuntimeException when order status is not found")
        void throwRuntimeExceptionWhenOrderStatusIsNotFound() {
            // Given
            String customerEmail = "duke.last@gmail.com";
            CartItem cartItem = CartItem.builder().build();
            Cart cart = Cart.builder().cartItems(Set.of(cartItem)).totalCost(10F).build();

            given(cartRepository.findByCustomer_Email(customerEmail)).willReturn(Optional.of(cart));
            given(orderStatusRepository.findByStatus(OrderStatusType.PROCESSING)).willReturn(Optional.empty());

            // When
            assertThatThrownBy(() -> cut.createCustomerOrder(customerEmail))
                    .hasMessage(ExceptionMessages.ORDER_STATUS_NOT_FOUND)
                    .isInstanceOf(RuntimeException.class);

            // Then
            then(orderRepository).shouldHaveNoInteractions();
            then(cartItemRepository).shouldHaveNoInteractions();
            then(cartRepository).should(never()).save(any(Cart.class));
        }
    }
}
