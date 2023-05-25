package com.abranlezama.ecommercestore.cart;

import com.abranlezama.ecommercestore.cart.dto.AddProductToCartDTO;
import com.abranlezama.ecommercestore.cart.dto.CartDTO;
import com.abranlezama.ecommercestore.exception.ConflictException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import com.abranlezama.ecommercestore.product.Product;
import com.abranlezama.ecommercestore.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerCartServiceImp implements CustomerCartService{

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    @Override
    public CartDTO retrieveCustomerCart(String customerEmail) {
        Cart cart = getCustomerCart(customerEmail);

        return cartMapper.mapCartToDto(cart);
    }

    @Override
    public void addProductToCart(AddProductToCartDTO addTdo, String customerEmail) {
        // get customer's cart
        Cart cart = getCustomerCart(customerEmail);

        // retrieve product and verify availability
        Product product = getProduct(addTdo.productId());
        if (addTdo.quantity() > product.getStockQuantity()) throw new ConflictException(ExceptionMessages.PRODUCT_OUT_OF_STOCK);

        CartItem cartItem = CartItem.builder()
                .quantity(addTdo.quantity())
                .cart(cart)
                .product(product)
                .build();

        // update cart
        cart.getCartItems().add(cartItem);
        cart.setTotalCost(calculateCustomerCartTotal(cart));
        cartRepository.save(cart);
    }

    private Cart getCustomerCart(String customerEmail) {
        return cartRepository.findByCustomer_Email(customerEmail)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.CART_NOT_FOUND));
    }

    private Product getProduct(long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.PRODUCT_NOT_FOUND));
    }

    public Float calculateCustomerCartTotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> item.getQuantity() * item.getProduct().getPrice())
                .reduce(0F, Float::sum);
    }
}
