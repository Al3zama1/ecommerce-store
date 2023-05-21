package com.abranlezama.ecommercestore.dto.order.mapper;

import com.abranlezama.ecommercestore.dto.order.OrderDTO;
import com.abranlezama.ecommercestore.model.Order;
import com.abranlezama.ecommercestore.model.OrderStatus;
import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {

    default String mapOrderStatusTypeToString(OrderStatus orderStatus) {
        return orderStatus.getStatus().name();
    }
    OrderDTO mapOrderToDto(Order order);
}
