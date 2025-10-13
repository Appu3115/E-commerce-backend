package com.example.ecpro.controller;

//import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecpro.model.Review;
import com.example.ecpro.service.ReviewService;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins= "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    // Submit a review
    @PostMapping("/submit")
    public ResponseEntity<?> submitReview(@RequestBody Review review) {
        return reviewService.submitReview(review);
    }
    
    @GetMapping("/product/{productId}")
    public ResponseEntity<?> getProductReviews(@PathVariable Integer productId) {
        return reviewService.getProductReviews(productId);
    }
    
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(@PathVariable Integer reviewId) {
        return reviewService.deleteReviewById(reviewId);
    }
    
    @GetMapping("/average/{productId}")
    public ResponseEntity<?> getAverageRating(@PathVariable Integer productId) {
        return reviewService.getAverageRating(productId);
    }
}

//// Get all reviews for a product
//@GetMapping("/product/{productId}")
//public ResponseEntity<List<Review>> getReviews(@PathVariable Integer productId) {
//  return ResponseEntity.ok(reviewService.getReviews(productId));
//}
