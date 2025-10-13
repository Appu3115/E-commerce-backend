package com.example.ecpro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecpro.model.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
	List<Order> findByUserId(Integer userId);

	List<Order> findByUserIdAndDeliveryStatus(Integer userId, String string);
}

