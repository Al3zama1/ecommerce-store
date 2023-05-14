package com.abranlezama.ecommercestore.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private CategoryType category;

    @ManyToMany(mappedBy = "productCategories")
    List<Product> products;
}
