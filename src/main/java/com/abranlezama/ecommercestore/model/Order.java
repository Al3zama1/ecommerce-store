package com.abranlezama.ecommercestore.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Float totalCost;
    private LocalDateTime datePlaced;
    private LocalDateTime dateShipped;
    private LocalDateTime dateReceived;
    @OneToOne
    private OrderStatus orderStatus;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "order")
    private Set<OrderItem> orderItems;
    @OneToOne
    private Customer customer;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
