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

import com.example.ecpro.model.ProductRequest;
import com.example.ecpro.model.ProductUpdateRequest;
import com.example.ecpro.service.ProductService;

@RestController
@RequestMapping("/api/admin/products")
@CrossOrigin(origins= "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    
    @PostMapping("/addProduct")
    public ResponseEntity<?> createProduct(@RequestBody ProductRequest productRequest) {
        return productService.createProduct(productRequest);

    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Integer id) {
        return productService.getProductById(id);
    }

    
    @GetMapping("/getByNameAndSubCategory")
    public ResponseEntity<?> getProductByNameAndSubCategory(
            @RequestParam String name,
            @RequestParam String subCategory
    ) {
        return productService.getItem(name, subCategory);
    }

    @GetMapping("/getBySubCategory")
    public ResponseEntity<?> getProductsBySubCategory(@RequestParam String subCategory) {
        return productService.getlist(subCategory);
    }

    
    @GetMapping("/getByname")
    public ResponseEntity<?> getProductsByname(@RequestParam String name) {
        return productService.getNameList(name);
    }

    @GetMapping("/getByCategory")
    public ResponseEntity<?> getProductsByCategory(@RequestParam String category) {
        return productService.getOverallList(category);
    }
    
    @GetMapping("/getAll")
    public ResponseEntity<?> getAll() {
        return productService.getAllProduct();
    }
    @PutMapping("/update")
    public ResponseEntity<?> updateProduct(
            @RequestParam String oldName,
            @RequestParam String oldSubCategory,
            @RequestBody ProductUpdateRequest request
    ) {
        return productService.updateProduct(oldName, oldSubCategory, request);
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteProduct(
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam String subCategory) {
        return productService.deleteProduct(name, category, subCategory);
    }

}
