package com.abranlezama.ecommercestore.model;


import jakarta.persistence.Column;
import lombok.*;

import java.io.Serializable;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemId implements Serializable {

    private Long product;

    private Long cart;


}
