package com.example.ecpro.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ecpro.model.Cart;
import com.example.ecpro.model.CartRequest;
import com.example.ecpro.model.Product;
import com.example.ecpro.repository.CartRepository;
import com.example.ecpro.repository.ProductRepository;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepo;
    
    @Autowired
    private ProductRepository productRepo;

    public ResponseEntity<?> addToCart(CartRequest request) {
        if (request == null || request.getUserId() == null || request.getProductId() == null) {
            return ResponseEntity.badRequest().body("User ID and Product ID are required.");
        }

        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            return ResponseEntity.badRequest().body("Quantity must be at least 1.");
        }

        // ✅ Check if Product exists before adding to cart
        Optional<Product> productOpt = productRepo.findById(request.getProductId());
        if (!productOpt.isPresent()) {
            return ResponseEntity.badRequest().body("Product not found with ID: " + request.getProductId());
        }

        // ✅ Check if cart item already exists (for this user + product)
        Optional<Cart> existingCartItem = cartRepo.findByUserIdAndProductId(request.getUserId(), request.getProductId());

        if (existingCartItem.isPresent()) {
            Cart cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + request.getQuantity());
            cartRepo.save(cartItem);
            return ResponseEntity.ok("Product quantity updated in cart.");
        } else {
            Cart newCartItem = new Cart();
            newCartItem.setUserId(request.getUserId());
            newCartItem.setProductId(request.getProductId());
            newCartItem.setQuantity(request.getQuantity());
            cartRepo.save(newCartItem);
            return ResponseEntity.ok("Product added to cart.");
        }
    }
    
    public ResponseEntity<?> viewCart(Integer userId) {
        if (userId == null) {
            return ResponseEntity.badRequest().body("User ID is required.");
        }

        List<Cart> cartItems = cartRepo.findByUserId(userId);

        if (cartItems.isEmpty()) {
            return ResponseEntity.ok("Cart is empty.");
        }

        return ResponseEntity.ok(cartItems);
    }
    
    public ResponseEntity<?> updateCartQuantity(Integer userId, Integer productId, String action) {
        if (userId == null || productId == null || action == null || action.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("User ID, Product ID, and Action are required.");
        }

        Optional<Cart> optionalCart = cartRepo.findByUserIdAndProductId(userId, productId);

        if (optionalCart.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found in cart.");
        }

        Cart cartItem = optionalCart.get();

        if (action.equalsIgnoreCase("increase")) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            Cart updatedCart = cartRepo.save(cartItem);
            return ResponseEntity.ok(updatedCart);

        } else if (action.equalsIgnoreCase("decrease")) {
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
                Cart updatedCart = cartRepo.save(cartItem);
                return ResponseEntity.ok(updatedCart);
            } else {
          
                cartRepo.delete(cartItem);
                return ResponseEntity.ok("Product removed from cart.");
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid action. Only 'increase' or 'decrease' allowed.");
        }
    }
    
    public ResponseEntity<?> deleteCartItem(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            return ResponseEntity.badRequest().body("User ID and Product ID are required.");
        }

        Optional<Cart> optionalCart = cartRepo.findByUserIdAndProductId(userId, productId);

        if (optionalCart.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found in cart.");
        }

        cartRepo.delete(optionalCart.get());
        return ResponseEntity.ok("Product removed from cart successfully.");
    }
}
