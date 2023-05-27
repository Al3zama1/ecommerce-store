package com.abranlezama.ecommercestore.cart.model;

import com.abranlezama.ecommercestore.product.model.Product;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "cart_id"}))
@IdClass(CartItemId.class)
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
@Builder
public class CartItem {

    @Id
    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    @ToString.Exclude
    private Cart cart;

    @Column(nullable = false)
    private Short quantity;

}
