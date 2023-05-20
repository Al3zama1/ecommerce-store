package com.abranlezama.ecommercestore.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemId implements Serializable {

    private Long product;

    private Long cart;


}
