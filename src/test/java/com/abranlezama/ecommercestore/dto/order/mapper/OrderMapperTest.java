package com.abranlezama.ecommercestore.dto.order.mapper;

import com.abranlezama.ecommercestore.dto.order.OrderDTO;
import com.abranlezama.ecommercestore.model.Order;
import com.abranlezama.ecommercestore.model.OrderStatus;
import com.abranlezama.ecommercestore.model.OrderStatusType;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @Test
    void shouldMapOrderToOrderDto() {
        // Given
        Order order = Order.builder()
                .id(1L)
                .datePlaced(LocalDateTime.now())
                .dateShipped(LocalDateTime.now().plusDays(2))
                .dateDelivered(LocalDateTime.now().plusDays(3))
                .totalCost(300F)
                .orderStatus(OrderStatus.builder().status(OrderStatusType.DELIVERED).build())
                .build();

        // When
        OrderDTO orderDto = mapper.mapOrderToDto(order);

        // When
        assertThat(orderDto.id()).isEqualTo(order.getId());
        assertThat(orderDto.datePlaced()).isEqualTo(order.getDatePlaced());
        assertThat(orderDto.dateShipped()).isEqualTo(order.getDateShipped());
        assertThat(orderDto.dateDelivered()).isEqualTo(order.getDateDelivered());
        assertThat(orderDto.totalCost()).isEqualTo(order.getTotalCost());
        assertThat(orderDto.orderStatus()).isEqualTo("DELIVERED");

    }

}
