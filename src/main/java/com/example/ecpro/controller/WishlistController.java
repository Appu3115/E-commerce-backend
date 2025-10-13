package com.example.ecpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecpro.service.WishlistService;

@RestController
@RequestMapping("/api/wishlist")
@CrossOrigin(origins= "*")
public class WishlistController {

    @Autowired
    private WishlistService wishlistService;

    @PostMapping("/add")
    public ResponseEntity<?> addToWishlist(@RequestParam Integer userId, @RequestParam Integer productId) {
        return wishlistService.addToWishlist(userId, productId);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getWishlist(@PathVariable Integer userId) {
        return wishlistService.getWishlistByUser(userId);
    }

    @DeleteMapping("/remove")
    public ResponseEntity<?> removeFromWishlist(@RequestParam Integer userId, @RequestParam Integer productId) {
        return wishlistService.removeFromWishlist(userId, productId);
    }
    
    @PostMapping("/moveToCart")
    public ResponseEntity<?> moveToCart(@RequestParam Integer userId, @RequestParam Integer productId) {
        return wishlistService.moveToCart(userId, productId);
    }

}
