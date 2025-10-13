package com.example.ecpro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "products")
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private Integer price;
    private Integer stock;
    private String category;
    private String subCategory; 
    private String Description;

	private String imageUrl;

    private int discountPercentage;
    private int discountAmount;
    private int finalPrice;
    private boolean active;
    
    
	@PrePersist
    @PreUpdate
    public void autoCalculate() {
        if (price == null) price = 0;
        if (stock == null) stock = 0;

        if (price < 500) {
            discountPercentage = 0;
        } else if (price < 1000) {
            discountPercentage = 5;
        } else if (price < 2000) {
            discountPercentage = 10;
        } else {
            discountPercentage = 15;
        }

        discountAmount = (price * discountPercentage) / 100;
        finalPrice = price - discountAmount;
        if (finalPrice < 0) finalPrice = 0;

        active = stock > 0;
    }
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}

	public Integer getStock() {
		return stock;
	}

	public void setStock(Integer stock) {
		this.stock = stock;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getSubCategory() {
		return subCategory;
	}

	public void setSubCategory(String subCategory) {
		this.subCategory = subCategory;
	}
	
//	public String getColor() {
//			return color;
//	}
//
//    public void setColor(String color) {
//			this.color = color;
//	}
	
	public String getDescription() {
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public int getDiscountPercentage() {
		return discountPercentage;
	}

	public void setDiscountPercentage(int discountPercentage) {
		this.discountPercentage = discountPercentage;
	}

	public int getDiscountAmount() {
		return discountAmount;
	}

	public void setDiscountAmount(int discountAmount) {
		this.discountAmount = discountAmount;
	}

	public int getFinalPrice() {
		return finalPrice;
	}

	public void setFinalPrice(int finalPrice) {
		this.finalPrice = finalPrice;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}
}



