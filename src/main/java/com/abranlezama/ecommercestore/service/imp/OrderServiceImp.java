package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.order.OrderResponseDTO;
import com.abranlezama.ecommercestore.service.OrderService;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class OrderServiceImp implements OrderService {
    @Override
    public Set<OrderResponseDTO> getCustomerOrders(String userEmail) {
        return null;
    }
}
