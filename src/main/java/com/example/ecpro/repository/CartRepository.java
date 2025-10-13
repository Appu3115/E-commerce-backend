package com.example.ecpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecpro.model.Cart;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUserId(int userId);
    Optional<Cart> findByUserIdAndProductId(int userId, int productId);
	boolean existsByUserIdAndProductId(Integer userId, Integer productId);
}
