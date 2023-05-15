package com.abranlezama.ecommercestore.service.imp;

import com.abranlezama.ecommercestore.dto.cart.CartDTO;
import com.abranlezama.ecommercestore.dto.cart.mapper.CartMapper;
import com.abranlezama.ecommercestore.exception.CustomerNotFound;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.ProductNotFoundException;
import com.abranlezama.ecommercestore.model.Cart;
import com.abranlezama.ecommercestore.model.CartItem;
import com.abranlezama.ecommercestore.model.Customer;
import com.abranlezama.ecommercestore.model.Product;
import com.abranlezama.ecommercestore.repository.CartItemRepository;
import com.abranlezama.ecommercestore.repository.CartRepository;
import com.abranlezama.ecommercestore.repository.CustomerRepository;
import com.abranlezama.ecommercestore.repository.ProductRepository;
import com.abranlezama.ecommercestore.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartServiceImp implements CartService {

    private final CustomerRepository customerRepository;
    private final CartItemRepository cartItemRepository;
    private final CartMapper cartMapper;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;

    @Override
    public CartDTO getCustomerCart(String userEmail) {
        // Retrieve customer
        Customer customer = retrieveCustomer(userEmail);
        return cartMapper.mapCartToDto(customer.getCart());
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
        // add new product to cart
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND));

        // save cart item
        CartItem newCartItem = new CartItem(product, customer.getCart(), quantity);
        customer.getCart().getCartItems().add(newCartItem);
        customer.getCart().setTotalCost(computeCartTotal(customer));
        cartRepository.save(customer.getCart());
    }

    @Override
    public void updateCartProduct(String userEmail, long productId, int quantity) {
        // get customer
        Customer customer = retrieveCustomer(userEmail);

        CartItem cartItem = customer.getCart().getCartItems().stream()
                .filter(item -> item.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND));

        cartItem.setQuantity(quantity);
        customer.getCart().setTotalCost(computeCartTotal(customer));

        cartRepository.save(customer.getCart());
    }

    private float computeCartTotal(Customer customer) {
        return customer.getCart().getCartItems().stream()
                .map(item -> item.getQuantity() * item.getProduct().getPrice())
                .reduce(0F, Float::sum);
    }

    private Customer retrieveCustomer(String userEmail) {
        return customerRepository.findByUser_Email(userEmail)
                .orElseThrow(() -> new CustomerNotFound(ExceptionMessages.CUSTOMER_NOT_FOUND));
    }
}
