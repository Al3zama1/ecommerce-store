package com.abranlezama.ecommercestore.objectmother;

import com.abranlezama.ecommercestore.product.Product;

public class ProductMother {

    public static Product.ProductBuilder complete() {
        return Product.builder()
                .id(1L)
                .name("Soccer Ball")
                .description("Next generation soccer ball.")
                .price(30F)
                .stockQuantity((short) 100);
    }
}
