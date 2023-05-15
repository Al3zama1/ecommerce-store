package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.dto.cart.mapper.CartMapper;
import com.abranlezama.ecommercestore.exception.CustomerNotFound;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImp implements CartService {

    private final CustomerRepository customerRepository;
    private final CartMapper cartMapper;
    @Override
    public CartDTO getCustomerCart(String userEmail) {
        // Retrieve customer
        Customer customer = customerRepository.findByUser_Email(userEmail)
                .orElseThrow(() -> new CustomerNotFound(ExceptionMessages.CUSTOMER_NOT_FOUND));

        CartDTO cartDto = cartMapper.mapCartToDto(customer.getCart());
        float cartTotal  = customer.getCart().getCartItems().stream()
                .map(item -> item.getQuantity() * item.getProduct().getPrice())
                .reduce(0F, Float::sum);
        cartDto.setCartTotal(cartTotal);

        return cartDto;
    }
}
