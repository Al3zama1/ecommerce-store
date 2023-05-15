package com.abranlezama.ecommercestore.dto.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO {
    @Valid private Set<CartItemDTO> cartItems;
    @PositiveOrZero private Float cartTotal;
}
