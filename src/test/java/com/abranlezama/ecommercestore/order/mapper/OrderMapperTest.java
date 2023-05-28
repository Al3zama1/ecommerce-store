package com.abranlezama.ecommercestore.order.mapper;

import com.abranlezama.ecommercestore.order.dto.OrderDTO;
import com.abranlezama.ecommercestore.order.model.Order;
import com.abranlezama.ecommercestore.order.model.OrderStatus;
import com.abranlezama.ecommercestore.order.util.OrderStatusType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("order mapper")
class OrderMapperTest {

    private final OrderMapper mapper = Mappers.getMapper(OrderMapper.class);

    @Test
    @DisplayName("convert from Order model to DTO")
    void mapOrderToOrderDto() {
        // Given
        Order order = Order.builder()
                .id(1L)
                .datePlaced(LocalDateTime.now())
                .dateShipped(LocalDateTime.now().plusDays(2))
                .dateDelivered(LocalDateTime.now().plusDays(3))
                .totalCost(300F)
                .status(OrderStatus.builder().status(OrderStatusType.PROCESSING).build())
                .build();

        // When
        OrderDTO orderDto = mapper.mapOrderToDto(order);

        // When
        assertThat(orderDto.id()).isEqualTo(order.getId());
        assertThat(orderDto.datePlaced()).isEqualTo(order.getDatePlaced());
        assertThat(orderDto.dateShipped()).isEqualTo(order.getDateShipped());
        assertThat(orderDto.dateDelivered()).isEqualTo(order.getDateDelivered());
        assertThat(orderDto.totalCost()).isEqualTo(order.getTotalCost());
        assertThat(orderDto.status()).isEqualTo("PROCESSING");

    }

}
