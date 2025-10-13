package com.example.ecpro.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ecpro.model.Product;


@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByNameAndCategoryAndSubCategory(String name, String category, String subCategory);
    
    Optional<Product> findByNameAndSubCategory(String name, String subCategory);
    
    Optional<Product> findByNameAndCategory(String category, String subCategory );
    
    List<Product> findByName(String name);

    List<Product> findBySubCategory(String subCategory);

    List<Product> findByCategory(String category);

	Optional<Product> findById(Integer id);

	Product getProductById(Integer productId);
    
}