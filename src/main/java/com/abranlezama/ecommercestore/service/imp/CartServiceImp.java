package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.dto.cart.mapper.CartMapper;
import com.abranlezama.ecommercestore.exception.CustomerNotFound;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.model.CartItem;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.model.Product;
import com.abranlezama.ecommercestore.repository.CartItemRepository;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.ProductRepository;
import com.abranlezama.ecommercestore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImp implements CartService {

    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;

    @Override
    public CartDTO getCustomerCart(String userEmail) {
        // Retrieve customer
        Customer customer = retrieveCustomer(userEmail);

        CartDTO cartDto = cartMapper.mapCartToDto(customer.getCart());
        float cartTotal  = customer.getCart().getCartItems().stream()
                .map(item -> item.getQuantity() * item.getProduct().getPrice())
                .reduce(0F, Float::sum);
        cartDto.setCartTotal(cartTotal);

        return cartDto;
    }

    @Override
    public void addProductToCart(String userEmail, long productId, int quantity) {
        // retrieve customer
        Customer customer = retrieveCustomer(userEmail);

        // increase product quantity if it is in cart already
        CartItem cartItem = customer.cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItemRepository.save(cartItem);
            return;
        }
        // add item to cart
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found."));

        CartItem newCartItem = new CartItem(product, customer.getCart(), quantity);
        cartItemRepository.save(newCartItem);

    }

    private Customer retrieveCustomer(String userEmail) {
        return customerRepository.findByUser_Email(userEmail)
                .orElseThrow(() -> new CustomerNotFound(ExceptionMessages.CUSTOMER_NOT_FOUND));
    }
}
