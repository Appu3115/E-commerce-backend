package com.example.ecpro.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.ecpro.model.Product;
import com.example.ecpro.model.ProductRequest;
import com.example.ecpro.model.ProductUpdateRequest;
import com.example.ecpro.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    public ResponseEntity<?> createProduct(ProductRequest request) {
        if (request == null) {
            return ResponseEntity.badRequest().body("Product data must not be null.");
        }

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Product name is required.");
        }
        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Category is required.");
        }
        if (request.getSubCategory() == null || request.getSubCategory().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Sub-category is required.");
        }
        if(request.getDescription() == null || request.getDescription().isEmpty()) {
        	return ResponseEntity.badRequest().body("Description is required");
        }
        if (request.getImageUrl() == null || request.getImageUrl().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Image URL is required.");
        }

        // Validate price
        if (request.getPrice() == null || request.getPrice().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Price is required.");
        }
        Integer price;
        try {
            price = Integer.parseInt(request.getPrice().trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Price must be a valid number.");
        }

       
        if (request.getStock() == null || request.getStock().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Stock is required.");
        }
        Integer stock;
        try {
            stock = Integer.parseInt(request.getStock().trim());
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Stock must be a valid number.");
        }

     
        Optional<Product> existingFull = productRepo.findByNameAndCategoryAndSubCategory(
                request.getName().trim(), request.getCategory().trim(), request.getSubCategory().trim()
        );
        if (existingFull.isPresent()) {
            return ResponseEntity.badRequest().body("Product already exists in this category and sub-category.");
        }

        
        Optional<Product> existingNameCategory = productRepo.findByNameAndCategory(
                request.getName().trim(), request.getCategory().trim()
        );
        if (existingNameCategory.isPresent()) {
            return ResponseEntity.badRequest().body("Product with same name and category already exists.");
        }

       
        Optional<Product> existingNameSubCategory = productRepo.findByNameAndSubCategory(
                request.getName().trim(), request.getSubCategory().trim()
        );
        if (existingNameSubCategory.isPresent()) {
            return ResponseEntity.badRequest().body("Product with same name and sub-category already exists.");
        }

        
        Product product = new Product();
        product.setName(request.getName().trim());
        product.setCategory(request.getCategory().trim());
        product.setSubCategory(request.getSubCategory().trim());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl().trim());
        product.setPrice(price);
        product.setStock(stock);

        product.autoCalculate();

        Product saved = productRepo.save(product);

        return ResponseEntity.ok(saved);
    }

    
    public ResponseEntity<?> getProductById(Integer id) {
        Optional<Product> product = productRepo.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        } else {
            return ResponseEntity.badRequest().body("Product not found with id: " + id);
        }
    }
    
    public ResponseEntity<?> getItem(String name, String subCategory) {
        if (name == null || name.trim().isEmpty() || subCategory == null || subCategory.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Name and Sub-category are required.");
        }

        Optional<Product> productOpt = productRepo.findByNameAndSubCategory(name.trim(), subCategory.trim());
        if (productOpt.isPresent()) {
            return ResponseEntity.ok(productOpt.get());
        } else {
            return ResponseEntity.badRequest().body("Product not found with given Name:"+ name +" and Sub-category:"+subCategory);
        }
    }
    
    public ResponseEntity<?> getNameList(String name){
    	if(name == null || name.trim().isEmpty()) {
    		return ResponseEntity.badRequest().body("Name is required.");
    	}
    	List<Product> products = productRepo.findByName(name.trim());
    	if(products.isEmpty()) {
    		return ResponseEntity.badRequest().body("No items matched for name: " + name);
    	}
    	return ResponseEntity.ok(products);
    }

    
    public ResponseEntity<?> getlist(String subCategory) {
        if (subCategory == null || subCategory.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Sub-category is required.");
        }

        List<Product> products = productRepo.findBySubCategory(subCategory.trim());

        if (products.isEmpty()) {
            return ResponseEntity.badRequest().body("No items matched for sub-category: " + subCategory);
        }

        return ResponseEntity.ok(products);
    }


    
    public ResponseEntity<?> getOverallList(String category) {
        if (category == null || category.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Category is required.");
        }

        List<Product> products = productRepo.findByCategory(category.trim());
        if (products.isEmpty()) {
            return ResponseEntity.badRequest().body("No items matched for sub-category: " + category);
        }
        return ResponseEntity.ok(products);
    }
    
    public ResponseEntity<?> getAllProduct() {
        List<Product> products = productRepo.findAll();
        // return empty list when no products — still 200 OK
        return ResponseEntity.ok(products != null ? products : Collections.emptyList());
    }


    
    public ResponseEntity<?> updateProduct(String oldName, String oldSubCategory, ProductUpdateRequest request) {
        if (oldName == null || oldSubCategory == null || oldName.trim().isEmpty() || oldSubCategory.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Old name and sub-category are required.");
        }

        Optional<Product> productOpt = productRepo.findByNameAndSubCategory(oldName.trim(), oldSubCategory.trim());

        if (productOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found with given Name and Sub-category.");
        }

        Product product = productOpt.get();
        boolean updated = false;

        if (request.getPrice() != null && !request.getPrice().trim().isEmpty()) {
            try {
                int price = Integer.parseInt(request.getPrice().trim());
                product.setPrice(price);
                updated = true;
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Price must be a valid number.");
            }
        }

        if (request.getStock() != null && !request.getStock().trim().isEmpty()) {
            try {
                int stock = Integer.parseInt(request.getStock().trim());
                product.setStock(stock);
                updated = true;
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body("Stock must be a valid number.");
            }
        }

        if (request.getImageUrl() != null && !request.getImageUrl().trim().isEmpty()) {
            product.setImageUrl(request.getImageUrl().trim());
            updated = true;
        }

        if (!updated) {
            return ResponseEntity.badRequest().body("No fields provided to update.");
        }

 
        product.autoCalculate();

        Product updatedProduct = productRepo.save(product);

        return ResponseEntity.ok(updatedProduct);
    }


    public ResponseEntity<?> deleteProduct(String name, String category, String subCategory) {
        if (name == null || name.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Product name is required.");
        }
        if (category == null || category.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Category is required.");
        }
        if (subCategory == null || subCategory.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Sub-category is required.");
        }

        Optional<Product> existing = productRepo.findByNameAndCategoryAndSubCategory(
                name.trim(), category.trim(), subCategory.trim()
        );

        if (existing.isEmpty()) {
            return ResponseEntity.badRequest().body("Product not found.");
        }

        try {
            productRepo.delete(existing.get());
            return ResponseEntity.ok("Product deleted successfully.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to delete product: " + e.getMessage());
        }
    }

}

