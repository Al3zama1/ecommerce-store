package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.order.mapper.OrderMapper;
import com.abranlezama.ecommercestore.model.Order;
import com.abranlezama.ecommercestore.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class OrderServiceImpTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private OrderMapper orderMapper;
    @InjectMocks
    private OrderServiceImp cut;

    @Test
    void shouldReturnCustomerOrders() {
        // Given
        String userEmail = "duke.last@gmail.com";
        PageRequest pageRequest = PageRequest.of(0, 20);
        List<Order> orders = List.of(Order.builder().build());
        Page<Order> orderPage = new PageImpl<>(orders);

        given(orderRepository.findAllByCustomer_User_Email(pageRequest, userEmail))
                .willReturn(orderPage);

        // When
        this.cut.getCustomerOrders(userEmail, pageRequest.getPageNumber(), pageRequest.getPageSize());

        // Then
        then(orderMapper).should().mapOrderToDto(any(Order.class));
    }

}
