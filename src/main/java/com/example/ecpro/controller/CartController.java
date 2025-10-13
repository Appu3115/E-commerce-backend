package com.example.ecpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecpro.model.CartRequest;
import com.example.ecpro.service.CartService;

@RestController
@RequestMapping("/cart")
@CrossOrigin(origins= "*")
public class CartController {

    @Autowired
    private CartService cartService;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest request) {
        return cartService.addToCart(request);
    }
    
    @GetMapping("/view/{userId}")
    public ResponseEntity<?> viewCart(@PathVariable Integer userId) {
        return cartService.viewCart(userId);
    }
    
    @PutMapping("/updateQuantity")
    public ResponseEntity<?> updateCartQuantity(
            @RequestParam Integer userId,
            @RequestParam Integer productId,
            @RequestParam String action
    ) {
        return cartService.updateCartQuantity(userId, productId, action);
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteCartItem(
            @RequestParam Integer userId,
            @RequestParam Integer productId
    ) {
        return cartService.deleteCartItem(userId, productId);
    }

}
