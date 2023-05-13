package com.abranlezama.ecommercestore.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Entity
@Table(name = "products")
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String name;

    @Column(length = 50)
    private String description;

    @Column(nullable = false)
    private Float price;

    @Column(nullable = false)
    private Integer stockQuantity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id) && Objects.equals(name, product.name) &&
                Objects.equals(description, product.description) && Objects.equals(price, product.price) &&
                Objects.equals(stockQuantity, product.stockQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, stockQuantity);
    }
}
