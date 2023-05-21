package com.abranlezama.ecommercestore.service;

import com.abranlezama.ecommercestore.dto.order.OrderResponseDTO;

import java.util.Set;

public interface OrderService {

    Set<OrderResponseDTO> getCustomerOrders(String userEmail, int page, int pageSize);
}
