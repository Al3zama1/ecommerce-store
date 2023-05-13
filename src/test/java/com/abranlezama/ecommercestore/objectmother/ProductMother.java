package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.model.Product;

public class ProductMother {

    public static Product.ProductBuilder complete() {
        return Product.builder()
                .id(1L)
                .name("PlayStation 5")
                .description("Next generation console.")
                .price(500F)
                .stockQuantity(300);
    }
}
