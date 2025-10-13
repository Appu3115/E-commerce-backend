package com.example.ecpro.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ecpro.model.LoginRequest;
import com.example.ecpro.model.LoginResponse;
import com.example.ecpro.model.PasswordResetToken;
import com.example.ecpro.model.RegisterRequest;
import com.example.ecpro.model.User;
import com.example.ecpro.repository.PassResetTokRepo;
import com.example.ecpro.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PassResetTokRepo tokenRepository;

    public ResponseEntity<String> registerUser(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Email already exists!");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match!");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    public ResponseEntity<?> loginUser(LoginRequest request) {
        Optional<User> userOpt = userRepository.findByEmail(request.getEmail());

        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User user = userOpt.get();

        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid email or password");
        }

        // Return user data (like userId) on successful login
        LoginResponse response = new LoginResponse();
        response.setId(user.getId());
        response.setEmail(user.getEmail());
        response.setUsername(user.getUsername());
        response.setMessage("Login successful!");

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<String> forgotPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("Email not found!");
        }

        User user = userOpt.get();

        List<PasswordResetToken> existingTokens = tokenRepository.findByUserId(user.getId());
        for (PasswordResetToken token : existingTokens) {
            token.setUsed(true);
        }
        tokenRepository.saveAll(existingTokens);

        String tokenStr = UUID.randomUUID().toString();
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(5);

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUserId(user.getId());
        resetToken.setToken(tokenStr);
        resetToken.setUsed(false);
        resetToken.setExpiryTime(expiryTime);
        tokenRepository.save(resetToken);

        return ResponseEntity.ok("Reset link (token): " + tokenStr);
    }

    public ResponseEntity<String> resetPassword(String token, String newPassword, String confirmPassword) {
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid token!");
        }

        PasswordResetToken resetToken = tokenOpt.get();

        if (resetToken.isUsed() || resetToken.getExpiryTime().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(410).body("Token is expired or already used!");
        }

        Optional<User> userOpt = userRepository.findById(resetToken.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found!");
        }

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body("Passwords do not match!");
        }

        User user = userOpt.get();
        user.setPassword(newPassword);
        userRepository.save(user);

        resetToken.setUsed(true);
        tokenRepository.save(resetToken);

        return ResponseEntity.ok("Password reset successful!");
    }
    
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();

        if (users.isEmpty()) {
            return ResponseEntity.ok("No users found.");
        }

        return ResponseEntity.ok(users);
    }
}
