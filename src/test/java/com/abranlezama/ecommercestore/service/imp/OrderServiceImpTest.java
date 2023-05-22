package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.order.mapper.OrderMapper;
import com.abranlezama.ecommercestore.model.Cart;
import com.abranlezama.ecommercestore.model.Order;
import com.abranlezama.ecommercestore.model.OrderStatus;
import com.abranlezama.ecommercestore.model.OrderStatusType;
import com.abranlezama.ecommercestore.repository.CartRepository;
import com.abranlezama.ecommercestore.repository.OrderRepository;
import com.abranlezama.ecommercestore.repository.OrderStatusRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderStatusRepository orderStatusRepository;
    @Mock
    private OrderMapper orderMapper;
    @Mock
    private CartRepository cartRepository;
    @InjectMocks
    private OrderServiceImp cut;

    @Test
    void shouldReturnCustomerOrders() {
        // Given
        String userEmail = "duke.last@gmail.com";
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<Order> orders = List.of(Order.builder().build());
        Page<Order> orderPage = new PageImpl<>(orders);

        given(orderRepository.findAllByCustomer_User_Email(pageRequest, userEmail))
                .willReturn(orderPage);

        // When
        this.cut.getCustomerOrders(userEmail, pageRequest.getPageNumber(), pageRequest.getPageSize());

        // Then
        then(orderMapper).should().mapOrderToDto(any(Order.class));
    }

    @Test
    void shouldCreateCustomerOrder() {
        // Given
        String userEmail = "duke.last@gmail.com";

        Cart cart = Cart.builder().cartItems(Set.of()).build();

        given(cartRepository.findByCustomer_User_Email(userEmail)).willReturn(Optional.of(cart));
        given(orderStatusRepository.findByStatus(OrderStatusType.PROCESSING))
                .willReturn(Optional.of(OrderStatus.builder().build()));
        given(orderRepository.save(any(Order.class))).willAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            savedOrder.setId(1L);
            return savedOrder;
        });

        // When
        cut.createOrder(userEmail);

        // Then
        then(orderRepository).should().save(any(Order.class));
    }

}
