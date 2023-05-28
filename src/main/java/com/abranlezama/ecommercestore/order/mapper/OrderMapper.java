package com.abranlezama.ecommercestore.order.mapper;

import com.abranlezama.ecommercestore.order.dto.OrderDTO;
import com.abranlezama.ecommercestore.order.model.Order;
import com.abranlezama.ecommercestore.order.model.OrderStatus;
import org.mapstruct.Mapper;

@Mapper
public interface OrderMapper {

    default String mapOrderStatusTypeToString(OrderStatus orderStatus) {
        return orderStatus.getStatus().name();
    }

    OrderDTO mapOrderToDto(Order order);
}
