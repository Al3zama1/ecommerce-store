package com.abranlezama.ecommercestore.cart.service.imp;

import com.abranlezama.ecommercestore.cart.model.Cart;
import com.abranlezama.ecommercestore.cart.model.CartItem;
import com.abranlezama.ecommercestore.cart.dto.AddProductToCartDTO;
import com.abranlezama.ecommercestore.cart.dto.CartDTO;
import com.abranlezama.ecommercestore.cart.dto.UpdateCartItemDTO;
import com.abranlezama.ecommercestore.cart.mapper.CartMapper;
import com.abranlezama.ecommercestore.cart.repository.CartItemRepository;
import com.abranlezama.ecommercestore.cart.repository.CartRepository;
import com.abranlezama.ecommercestore.cart.service.CustomerCartService;
import com.abranlezama.ecommercestore.exception.ConflictException;
import com.abranlezama.ecommercestore.exception.ExceptionMessages;
import com.abranlezama.ecommercestore.exception.NotFoundException;
import com.abranlezama.ecommercestore.product.model.Product;
import com.abranlezama.ecommercestore.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerCartServiceImp implements CustomerCartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
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

    @Override
    public void updateCartItem(UpdateCartItemDTO updateDto, String customerEmail) {
        // get customer cart and item to update
        Cart cart = getCustomerCart(customerEmail);
        CartItem cartItem = getCustomerCartItem(cart, updateDto.productId());

        // ensure enough inventory
        if (updateDto.quantity() > cartItem.getProduct().getStockQuantity()) throw new ConflictException(ExceptionMessages.PRODUCT_OUT_OF_STOCK);

        // make updated
        cartItem.setQuantity(updateDto.quantity());
        cart.setTotalCost(calculateCustomerCartTotal(cart));
        cartRepository.save(cart);
    }

    @Override
    public void removeItemFromCustomerCart(long productId, String customerEmail) {
        // get customer cart and cart item to remove
        Cart cart = getCustomerCart(customerEmail);
        CartItem cartItem = getCustomerCartItem(cart, productId);

        // remove cart item
        cartItemRepository.delete(cartItem);
        // update cart
        cart.getCartItems().remove(cartItem);
        cart.setTotalCost(calculateCustomerCartTotal(cart));
        cartRepository.save(cart);
    }

    private CartItem getCustomerCartItem(Cart cart, long productId) {
        return cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getProduct().getId() == productId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException(ExceptionMessages.CART_ITEM_NOT_FOUND));
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
