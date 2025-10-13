package com.example.ecpro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecpro.model.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByProductId(Integer productId);

    List<Review> findByUserIdAndProductIdAndOrderId(Integer userId, Integer productId, Integer orderId);
}