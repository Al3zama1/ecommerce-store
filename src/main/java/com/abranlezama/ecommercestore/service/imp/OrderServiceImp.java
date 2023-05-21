package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.order.OrderDTO;
import com.abranlezama.ecommercestore.dto.order.mapper.OrderMapper;
import com.abranlezama.ecommercestore.model.Order;
import com.abranlezama.ecommercestore.repository.OrderRepository;
import com.abranlezama.ecommercestore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImp implements OrderService {

    private final OrderRepository orderRepository;
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
        return 0;
    }
}
