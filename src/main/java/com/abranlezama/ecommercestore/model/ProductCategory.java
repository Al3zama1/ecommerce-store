package com.abranlezama.ecommercestore.model;

import jakarta.persistence.*;

@Entity
@Table(name = "product_categories")
public class ProductCategory {

    @EmbeddedId
    private ProductCategoryId id;
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("category_id")
    private Category category;
}
