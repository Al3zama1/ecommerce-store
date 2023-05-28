package com.abranlezama.ecommercestore.order.service.imp;

import com.abranlezama.ecommercestore.cart.model.Cart;
import com.abranlezama.ecommercestore.cart.repository.CartItemRepository;
import com.abranlezama.ecommercestore.cart.repository.CartRepository;
import com.abranlezama.ecommercestore.customer.repository.CustomerRepository;
import com.abranlezama.ecommercestore.exception.BadRequestException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import com.abranlezama.ecommercestore.order.model.Order;
import com.abranlezama.ecommercestore.order.model.OrderItem;
import com.abranlezama.ecommercestore.order.model.OrderStatus;
import com.abranlezama.ecommercestore.order.repository.OrderRepository;
import com.abranlezama.ecommercestore.order.repository.OrderStatusRepository;
import com.abranlezama.ecommercestore.order.service.CustomerOrderService;
import com.abranlezama.ecommercestore.order.util.OrderStatusType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerOrderServiceImp implements CustomerOrderService {

    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final OrderRepository orderRepository;

    @Override
    public long createCustomerOrder(String customerEmail) {
        Cart cart = getCustomerCart(customerEmail);

        if (cart.getCartItems().size() == 0 || cart.getTotalCost() <= 0)
            throw new BadRequestException(ExceptionMessages.EMPTY_ORDER);

        OrderStatus orderStatus = orderStatusRepository.findByStatus(OrderStatusType.PROCESSING)
                .orElseThrow(() -> new RuntimeException(ExceptionMessages.ORDER_STATUS_NOT_FOUND));

        Order order = Order.builder()
                .customer(cart.getCustomer())
                .status(orderStatus)
                .datePlaced(LocalDateTime.now())
                .totalCost(cart.getTotalCost())
                .build();
        Set<OrderItem> orderItems = generateOrderItems(cart, order);
        order.setOrderItems(orderItems);

        order = orderRepository.save(order);



        // reset customer cart
        cartItemRepository.deleteAll(cart.getCartItems());
        cart.getCartItems().clear();
        cart.setTotalCost(0F);
        cartRepository.save(cart);

        return order.getId();
    }

    private Set<OrderItem> generateOrderItems(Cart cart, Order order) {
        return cart.getCartItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .price(cartItem.getProduct().getPrice())
                        .quantity(cartItem.getQuantity())
                        .product(cartItem.getProduct())
                        .order(order)
                        .build())
                .collect(Collectors.toSet());
    }

    private Cart getCustomerCart(String customerEmail) {
        return cartRepository.findByCustomer_Email(customerEmail)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.CART_NOT_FOUND));
    }
}
