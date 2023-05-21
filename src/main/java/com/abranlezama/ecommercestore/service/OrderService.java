package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.order.OrderDTO;

import java.util.Set;

public interface OrderService {

    Set<OrderDTO> getCustomerOrders(String userEmail, int page, int pageSize);
}
