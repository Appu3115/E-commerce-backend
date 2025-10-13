package com.example.ecpro.model;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Integer userId;
    private Integer totalAmount;
    private String billingName;
    private String address;
    private String contactNumber;
    private String paymentMethod;
    private String status = "PENDING";
    private Boolean cancelStatus = false;
    private String cancelReason;
    private String returnStatus;
    private String returnReason;
    private Boolean replacementStatus = false;
    private String replacementReason;
    private String deliveryStatus = "PLACED"; // new field
    private Boolean refundInitiated = false;
    private Boolean refundCompleted = false;
    private String pickupStatus; // for return/replacement pickups
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date returnRequestedAt;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
    
    @Transient
    private List<OrderItem> orderItems;

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Integer getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Integer totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getBillingName() {
		return billingName;
	}

	public void setBillingName(String billingName) {
		this.billingName = billingName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getContactNumber() {
		return contactNumber;
	}

	public void setContactNumber(String contactNumber) {
		this.contactNumber = contactNumber;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Boolean getCancelStatus() {
		return cancelStatus;
	}

	public void setCancelStatus(Boolean cancelStatus) {
		this.cancelStatus = cancelStatus;
	}

	public String getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(String cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getReturnStatus() {
		return returnStatus;
	}

	public void setReturnStatus(String string) {
		this.returnStatus = string;
	}

	public Date getReturnRequestedAt() {
		return returnRequestedAt;
	}

	public void setReturnRequestedAt(Date returnRequestedAt) {
		this.returnRequestedAt = returnRequestedAt;
	}

	public String getReturnReason() {
		return returnReason;
	}

	public void setReturnReason(String returnReason) {
		this.returnReason = returnReason;
	}

	public Boolean getReplacementStatus() {
		return replacementStatus;
	}

	public void setReplacementStatus(Boolean replacementStatus) {
		this.replacementStatus = replacementStatus;
	}

	public String getReplacementReason() {
		return replacementReason;
	}

	public void setReplacementReason(String replacementReason) {
		this.replacementReason = replacementReason;
	}

	public String getDeliveryStatus() {
		return deliveryStatus;
	}

	public void setDeliveryStatus(String deliveryStatus) {
		this.deliveryStatus = deliveryStatus;
	}

	public Boolean getRefundInitiated() {
		return refundInitiated;
	}

	public void setRefundInitiated(Boolean refundInitiated) {
		this.refundInitiated = refundInitiated;
	}

	public Boolean getRefundCompleted() {
		return refundCompleted;
	}

	public void setRefundCompleted(Boolean refundCompleted) {
		this.refundCompleted = refundCompleted;
	}

	public String getPickupStatus() {
		return pickupStatus;
	}

	public void setPickupStatus(String pickupStatus) {
		this.pickupStatus = pickupStatus;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

}
