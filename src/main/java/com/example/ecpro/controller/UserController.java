package com.example.ecpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecpro.model.LoginRequest;
import com.example.ecpro.model.RegisterRequest;
import com.example.ecpro.service.UserService;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins= "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        return userService.registerUser(request);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }

    @PutMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        return userService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
        @RequestParam String token,
        @RequestParam String newPassword,
        @RequestParam String confirmPassword
    ) {
        return userService.resetPassword(token, newPassword, confirmPassword);
    }
    
    @GetMapping("/getAllUsers")
    public ResponseEntity<?> getAllUsers() {
        return userService.getAllUsers();
    }
}
