package com.example.ecpro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecpro.model.Cart;
import com.example.ecpro.model.Product;
import com.example.ecpro.model.Wishlist;
import com.example.ecpro.repository.CartRepository;
import com.example.ecpro.repository.ProductRepository;
import com.example.ecpro.repository.WishlistRepository;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepo;

    @Autowired
    private ProductRepository productRepo;
    
    @Autowired
    private CartRepository cartRepo;

    public ResponseEntity<?> addToWishlist(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            return ResponseEntity.badRequest().body("User ID and Product ID are required.");
        }

        if (!productRepo.existsById(productId)) {
            return ResponseEntity.badRequest().body("Product not found with ID: " + productId);
        }

        if (wishlistRepo.existsByUserIdAndProductId(userId, productId)) {
            return ResponseEntity.badRequest().body("Product already in wishlist.");
        }

        Wishlist wishlist = new Wishlist();
        wishlist.setUserId(userId);
        wishlist.setProductId(productId);
        wishlistRepo.save(wishlist);

        return ResponseEntity.ok("Product added to wishlist successfully!");
    }

    public ResponseEntity<?> getWishlistByUser(Integer userId) {
        List<Wishlist> wishlistItems = wishlistRepo.findByUserId(userId);

        if (wishlistItems.isEmpty()) {
            return ResponseEntity.ok("Your wishlist is empty.");
        }

        List<Product> products = wishlistItems.stream()
                .map(item -> productRepo.findById(item.getProductId()).orElse(null))
                .filter(product -> product != null)
                .toList();

        return ResponseEntity.ok(products);
    }


    @Transactional
    public ResponseEntity<?> removeFromWishlist(Integer userId, Integer productId) {
        if (!wishlistRepo.existsByUserIdAndProductId(userId, productId)) {
            return ResponseEntity.badRequest().body("Product not found in wishlist.");
        }

        wishlistRepo.deleteByUserIdAndProductId(userId, productId);
        return ResponseEntity.ok("Product removed from wishlist.");
    }
    
    @Transactional
    public ResponseEntity<?> moveToCart(Integer userId, Integer productId) {
        if (userId == null || productId == null) {
            return ResponseEntity.badRequest().body("User ID and Product ID are required.");
        }

        // Check if product is in wishlist
        if (!wishlistRepo.existsByUserIdAndProductId(userId, productId)) {
            return ResponseEntity.badRequest().body("Product not found in wishlist.");
        }

        // Check if product exists and is in stock
        Product product = productRepo.findById(productId).orElse(null);
        if (product == null || product.getStock() <= 0) {
            return ResponseEntity.badRequest().body("Product not available.");
        }

        // Check if product already in cart
        boolean existsInCart = cartRepo.existsByUserIdAndProductId(userId, productId);
        if (existsInCart) {
            return ResponseEntity.badRequest().body("Product already exists in cart.");
        }

        // Create new Cart entry
        Cart cart = new Cart();
        cart.setUserId(userId);
        cart.setProductId(productId);
        cart.setQuantity(1); // Default quantity 1
        cartRepo.save(cart);

        // Remove from wishlist
        wishlistRepo.deleteByUserIdAndProductId(userId, productId);

        return ResponseEntity.ok("Product moved from wishlist to cart successfully!");
    }

}
