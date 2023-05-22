package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.order.OrderDTO;
import com.abranlezama.ecommercestore.dto.order.mapper.OrderMapper;
import com.abranlezama.ecommercestore.exception.EmptyOrderException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.model.*;
import com.abranlezama.ecommercestore.repository.CartRepository;
import com.abranlezama.ecommercestore.repository.OrderItemRepository;
import com.abranlezama.ecommercestore.repository.OrderRepository;
import com.abranlezama.ecommercestore.repository.OrderStatusRepository;
import com.abranlezama.ecommercestore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final CartRepository cartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;

    @Override
    public Set<OrderDTO> getCustomerOrders(String userEmail, int page, int pageSize) {
        Page<Order> orders = orderRepository
                .findAllByCustomer_User_Email(PageRequest.of(page, pageSize), userEmail);

        return orders.stream()
                .map(orderMapper::mapOrderToDto)
                .collect(Collectors.toSet());
    }

    @Override
    public long createOrder(String userEmail) {
        // retrieve customer cart
        Cart cart = cartRepository.findByCustomer_User_Email(userEmail)
                .orElseThrow(() -> new RuntimeException("Cart not found"));

        if (cart.getCartItems().size() == 0) throw new EmptyOrderException(ExceptionMessages.EMPTY_ORDER);

        // generate order
        OrderStatus orderStatus = orderStatusRepository.findByStatus(OrderStatusType.PROCESSING)
                .orElseThrow(() -> new RuntimeException("Order status not found"));

        // create and save order
        Order order = Order.builder()
                .totalCost(cart.getTotalCost())
                .datePlaced(LocalDateTime.now())
                .orderStatus(orderStatus)
                .customer(cart.getCustomer())
                .build();
        order.setOrderItems(createOrderItems(cart, order));
        order = orderRepository.save(order);

        // reset customer shopping cart
        resetCustomerCart(cart);

        return order.getId();
    }

    private void resetCustomerCart(Cart cart) {
        cart.getCartItems().clear();
        cart.setTotalCost(0F);
        cartRepository.save(cart);
    }

    private Set<OrderItem> createOrderItems(Cart cart, Order order) {
        return cart.getCartItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .quantity(cartItem.getQuantity())
                        .product(cartItem.getProduct())
                        .price(cartItem.getProduct().getPrice())
                        .build())
                .collect(Collectors.toSet());
    }
}
