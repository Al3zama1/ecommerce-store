package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.CartDTO;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerCartServiceImp implements CustomerCartService{

    private final CartRepository cartRepository;
    private final CartMapper cartMapper;
    @Override
    public CartDTO retrieveCustomerCart(String customerEmail) {
        Cart cart = cartRepository.findByCustomer_Email(customerEmail)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.CART_NOT_FOUND));

        return cartMapper.mapCartToDto(cart);
    }
}
