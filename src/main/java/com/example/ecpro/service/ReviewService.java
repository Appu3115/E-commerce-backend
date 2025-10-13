package com.example.ecpro.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecpro.model.Order;
import com.example.ecpro.model.OrderItem;
import com.example.ecpro.model.Review;
import com.example.ecpro.model.User;
import com.example.ecpro.repository.OrderItemRepository;
import com.example.ecpro.repository.OrderRepository;
import com.example.ecpro.repository.ReviewRepository;
import com.example.ecpro.repository.UserRepository;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public ResponseEntity<?> submitReview(Review review) {
        if (review.getRating() < 1 || review.getRating() > 5) {
            return ResponseEntity.badRequest().body("Rating must be between 1 and 5.");
        }

//        if (review.getId() != null) {
//            return ResponseEntity.badRequest().body("Review ID must not be provided for new submissions.");
//        }

        Integer userId = review.getUserId();
        Integer productId = review.getProductId();
        Integer orderId = review.getOrderId();

        List<Review> existingReviews = reviewRepo.findByUserIdAndProductIdAndOrderId(userId, productId, orderId);
        if (!existingReviews.isEmpty()) {
            return ResponseEntity.badRequest().body("You have already reviewed this product.");
        }

        List<Order> deliveredOrders = orderRepo.findByUserIdAndDeliveryStatus(userId, "DELIVERED");
        boolean hasPurchasedProduct = false;

        for (Order order : deliveredOrders) {
            List<OrderItem> items = orderItemRepo.findByOrderId(order.getId());
            for (OrderItem item : items) {
                if (item.getProductId().equals(productId) && item.getOrderId().equals(orderId)) {
                    hasPurchasedProduct = true;
                    break;
                }
            }
            if (hasPurchasedProduct) break;
        }

        if (!hasPurchasedProduct) {
            return ResponseEntity.badRequest().body("You can only review products you have purchased and received.");
        }

        review.setId(null); // ensure it's treated as a new review
        reviewRepo.save(review);
        return ResponseEntity.ok("Review submitted successfully.");
    }
    
    public ResponseEntity<?> getProductReviews(Integer productId) {
        List<Review> reviews = reviewRepo.findByProductId(productId);

        // ✅ If no reviews, return empty list instead of throwing error or null
        if (reviews == null || reviews.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Map<String, Object>> response = new ArrayList<>();

        for (Review review : reviews) {
            Optional<User> userOpt = userRepository.findById(review.getUserId());

            String username = userOpt.map(User::getUsername).orElse("Unknown");

            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("username", username);
            reviewMap.put("comment", review.getComment());
            reviewMap.put("rating", review.getRating());
            reviewMap.put("createdAt", review.getCreatedAt());
            reviewMap.put("reviewId", review.getId());

            response.add(reviewMap);
        }

        return ResponseEntity.ok(response);
    }



    public ResponseEntity<?> deleteReviewById(Integer reviewId) {
        Optional<Review> optionalReview = reviewRepo.findById(reviewId);
        if (optionalReview.isEmpty()) {
            return ResponseEntity.badRequest().body("Review not found.");
        }

        reviewRepo.deleteById(reviewId);
        return ResponseEntity.ok("Review deleted successfully.");
    }
    
    public ResponseEntity<?> getAverageRating(Integer productId) {
        List<Review> reviews = reviewRepo.findByProductId(productId);

        if (reviews.isEmpty()) {
            return ResponseEntity.badRequest()
                                 .body("No reviews found for this product.");
        }

        double average = reviews.stream()
                                .mapToInt(Review::getRating)
                                .average()
                                .orElse(0.0);

        Map<String, Object> response = new HashMap<>();
        response.put("productId", productId);
        response.put("averageRating", average);

        return ResponseEntity.ok(response);
    }

}


//public List<Review> getReviews(Integer productId) {
//return reviewRepo.findByProductId(productId);
//}
