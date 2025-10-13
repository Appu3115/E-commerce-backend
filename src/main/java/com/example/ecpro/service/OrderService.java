package com.example.ecpro.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.ecpro.model.Cart;
import com.example.ecpro.model.Order;
import com.example.ecpro.model.OrderItem;
import com.example.ecpro.model.OrderRequest;
import com.example.ecpro.model.Product;
import com.example.ecpro.repository.CartRepository;
import com.example.ecpro.repository.OrderItemRepository;
import com.example.ecpro.repository.OrderRepository;
import com.example.ecpro.repository.ProductRepository;

@Service
public class OrderService {

    @Autowired
    private CartRepository cartRepo;

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;
    
    @Autowired
    private ProductRepository productRepo;

    @Transactional
    public ResponseEntity<?> placeOrder(OrderRequest request) {
        if (request.getUserId() == null || request.getAddress() == null || request.getContactNumber() == null
                || request.getPaymentMethod() == null || request.getBillingName() == null) {
            return ResponseEntity.badRequest().body("All fields are required (User ID, Address, Contact Number, Payment Method, Billing Name).");
        }

     // Validate Contact Number
        String contact = request.getContactNumber().trim();
        if (!contact.matches("\\d{10}")) {
            return ResponseEntity.badRequest().body("Contact Number must be exactly 10 digits and numeric only.");
        }
        
        List<Cart> cartItems = cartRepo.findByUserId(request.getUserId());

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty. Cannot place order.");
        }

        int totalAmount = 0;

        for (Cart item : cartItems) {
            Product product = productRepo.findById(item.getProductId())
                    .orElse(null);

            if (product == null) {
                return ResponseEntity.badRequest().body("Product not found with ID: " + item.getProductId());
            }

            if (product.getStock() < item.getQuantity()) {
                return ResponseEntity.badRequest().body("Not enough stock for product: " + product.getName());
            }

            totalAmount += item.getQuantity() * product.getFinalPrice();
        }

        // Create Order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotalAmount(totalAmount);
        order.setStatus("PLACED");
        order.setAddress(request.getAddress());
        order.setContactNumber(request.getContactNumber());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setBillingName(request.getBillingName());
        orderRepo.save(order);

        // Save OrderItems and Update Product Stock
        for (Cart cartItem : cartItems) {
            Product product = productRepo.findById(cartItem.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + cartItem.getProductId()));

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPricePerUnit(product.getFinalPrice());
            orderItem.setTotalPrice(cartItem.getQuantity() * product.getFinalPrice());
            orderItemRepo.save(orderItem);

            // Decrease product stock
            product.setStock(product.getStock() - cartItem.getQuantity());
            productRepo.save(product);
        }

        // Empty Cart
        cartRepo.deleteAll(cartItems);

        return ResponseEntity.ok("Order placed successfully! Order ID: " + order.getId());
    }
    
    @Transactional
    public ResponseEntity<?> BuyNow(OrderRequest request, Integer productId, Integer quantity) {
        if (request.getUserId() == null || productId == null || quantity == null || quantity <= 0
                || request.getAddress() == null || request.getContactNumber() == null
                || request.getPaymentMethod() == null || request.getBillingName() == null) {
            return ResponseEntity.badRequest().body("All fields are required (User ID, Product ID, Quantity, Address, Contact Number, Payment Method, Billing Name).");
        }

        // Validate Contact Number
        String contact = request.getContactNumber().trim();
        if (!contact.matches("\\d{10}")) {
            return ResponseEntity.badRequest().body("Contact Number must be exactly 10 digits and numeric only.");
        }

        // Find Product
        Product product = productRepo.findById(productId).orElse(null);
        if (product == null) {
            return ResponseEntity.badRequest().body("Product not found with ID: " + productId);
        }

        // Check Stock
        if (product.getStock() < quantity) {
            return ResponseEntity.badRequest().body("Not enough stock for product: " + product.getName());
        }

        // Calculate Total Amount
        int totalAmount = quantity * product.getFinalPrice();

        // Create and Save Order
        Order order = new Order();
        order.setUserId(request.getUserId());
        order.setTotalAmount(totalAmount);
        order.setStatus("PLACED");
        order.setAddress(request.getAddress());
        order.setContactNumber(request.getContactNumber());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setBillingName(request.getBillingName());
        orderRepo.save(order);

        // Create and Save OrderItem
        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(order.getId());
        orderItem.setProductId(productId);
        orderItem.setQuantity(quantity);
        orderItem.setPricePerUnit(product.getFinalPrice());
        orderItem.setTotalPrice(totalAmount);
        orderItemRepo.save(orderItem);

        // Update Product Stock
        product.setStock(product.getStock() - quantity);
        productRepo.save(product);

        // Response
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Product ordered successfully!");
        response.put("orderId", order.getId());
        response.put("productName", product.getName());
        response.put("totalAmount", totalAmount);

        return ResponseEntity.ok(response);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getOrdersByUser(Integer userId) {
        List<Order> orders = orderRepo.findByUserId(userId);

        if (orders.isEmpty()) {
            return ResponseEntity.ok("No orders found for this user.");
        }

        for (Order order : orders) {
            List<OrderItem> items = orderItemRepo.findByOrderId(order.getId());
            order.setOrderItems(items);
        }

        return ResponseEntity.ok(orders);
    }

    @Transactional(readOnly = true)
    public ResponseEntity<?> getAllOrders() {
        List<Order> orders = orderRepo.findAll();

        if (orders.isEmpty()) {
            return ResponseEntity.ok("No orders found.");
        }

        for (Order order : orders) {
            List<OrderItem> items = orderItemRepo.findByOrderId(order.getId());
            order.setOrderItems(items);
        }

        return ResponseEntity.ok(orders);
    }
    
    @Transactional
    public ResponseEntity<?> cancelOrder(Integer orderId, String reason) {
        Order order = orderRepo.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.badRequest().body("Order not found with ID: " + orderId);
        }

        String currentDeliveryStatus = order.getDeliveryStatus();

        if (!"PLACED".equals(currentDeliveryStatus) && !"PACKING".equals(currentDeliveryStatus)) {
            return ResponseEntity.badRequest().body("Sorry, your order is already shipped or out for delivery. You cannot cancel now.");
        }

        order.setStatus("CANCELLED");
        order.setCancelReason(reason);
        order.setCancelStatus(true);  // Mark the cancellation status as true

        // Restock products
        List<OrderItem> orderItems = orderItemRepo.findByOrderId(orderId);
        for (OrderItem item : orderItems) {
            Product product = productRepo.findById(item.getProductId()).orElse(null);
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productRepo.save(product);
            }
        }

        orderRepo.save(order);

        return ResponseEntity.ok("Order cancelled successfully! & Refund initiated");
    }
    
    @Transactional
    public ResponseEntity<?> updateOrderDeliveryStatus(Integer orderId, String newStatus) {
        Order order = orderRepo.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.badRequest().body("Order not found with ID: " + orderId);
        }

        String currentStatus = order.getDeliveryStatus();

        if ("DELIVERED".equalsIgnoreCase(currentStatus)) {
            return ResponseEntity.ok("Order is already delivered. No further updates needed.");
        }

        if (newStatus != null && !newStatus.isEmpty()) {
            // Manual update
            order.setDeliveryStatus(newStatus.toUpperCase());
            orderRepo.save(order);
            return ResponseEntity.ok("Order status manually updated to: " + order.getDeliveryStatus());
        }

        // Automatic update
        long now = System.currentTimeMillis();
        long orderTime = order.getCreatedAt().getTime();
        long minutesPassed = (now - orderTime) / (1000 * 60);

        switch (currentStatus) {
            case "PLACED":
                if (minutesPassed >= 1) {
                    order.setDeliveryStatus("PACKING");
                } else {
                    return ResponseEntity.ok("Waiting for auto-update from PLACED → PACKING...");
                }
                break;

            case "PACKING":
                if (minutesPassed >= 2) {
                    order.setDeliveryStatus("SHIPPING");
                } else {
                    return ResponseEntity.ok("Waiting for auto-update from PACKING → SHIPPING...");
                }
                break;

            case "SHIPPING":
                if (minutesPassed >= 3) {
                    order.setDeliveryStatus("OUT_FOR_DELIVERY");
                } else {
                    return ResponseEntity.ok("Waiting for auto-update from SHIPPING → OUT_FOR_DELIVERY...");
                }
                break;

            case "OUT_FOR_DELIVERY":
                if (minutesPassed >= 4) {
                    order.setDeliveryStatus("DELIVERED");
                } else {
                    return ResponseEntity.ok("Waiting for auto-update from OUT_FOR_DELIVERY → DELIVERED...");
                }
                break;

            default:
                return ResponseEntity.ok("No valid status transition available for: " + currentStatus);
        }

        orderRepo.save(order);
        return ResponseEntity.ok("Order status auto-updated to: " + order.getDeliveryStatus());
    }


    @Transactional
    public ResponseEntity<?> returnOrder(Integer orderId, String reason) {
        Order order = orderRepo.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.badRequest().body("Order not found with ID: " + orderId);
        }

        if (!"DELIVERED".equals(order.getDeliveryStatus())) {
            return ResponseEntity.badRequest().body("You can only return a product after it is delivered.");
        }

        if (!"PLACED".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body("Cannot return an order that is already cancelled, returned, or replaced.");
        }

        order.setStatus("RETURN_REQUESTED");
        order.setReturnStatus("PICKUP_PENDING");
        order.setReturnReason(reason);
        order.setReturnRequestedAt(new Date());
        orderRepo.save(order);

        return ResponseEntity.ok("Return request submitted successfully! Return process started.");
    }
    
    @Transactional
    public ResponseEntity<?> updateReturnStatus(Integer orderId, String newReturnStatus) {
        Order order = orderRepo.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.badRequest().body("Order not found with ID: " + orderId);
        }

        if (!"RETURN_REQUESTED".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body("This order is not in return process.");
        }

        if (order.getReturnRequestedAt() == null) {
            return ResponseEntity.badRequest().body("Return timestamp not found. Cannot update return status.");
        }

        String currentReturnStatus = order.getReturnStatus();

        // ✅ Manual status update
        if (newReturnStatus != null && !newReturnStatus.isEmpty()) {
            if ("REFUND_COMPLETED".equalsIgnoreCase(currentReturnStatus)) {
                return ResponseEntity.badRequest().body("Return process already completed.");
            }

            order.setReturnStatus(newReturnStatus.toUpperCase());

            if ("REFUND_COMPLETED".equalsIgnoreCase(newReturnStatus)) {
                order.setStatus("RETURNED");
            }

            orderRepo.save(order);
            return ResponseEntity.ok("Return status manually updated to: " + order.getReturnStatus());
        }

        // ✅ Automatic update based on time
        long now = System.currentTimeMillis();
        long returnRequestedTime = order.getReturnRequestedAt().getTime();
        long minutesPassed = (now - returnRequestedTime) / (1000 * 60);

        if ("REFUND_COMPLETED".equals(currentReturnStatus)) {
            return ResponseEntity.badRequest().body("Return process already completed.");
        }

        if (minutesPassed >= 4 && "REFUND_INITIATED".equals(currentReturnStatus)) {
            order.setReturnStatus("REFUND_COMPLETED");
            order.setStatus("RETURNED");
        } else if (minutesPassed >= 3 && "PICKED_UP".equals(currentReturnStatus)) {
            order.setReturnStatus("REFUND_INITIATED");
        } else if (minutesPassed >= 2 && "PICKUP_PENDING".equals(currentReturnStatus)) {
            order.setReturnStatus("PICKED_UP");
        } else if (minutesPassed >= 1 && (currentReturnStatus == null || "RETURN_REQUESTED".equals(currentReturnStatus))) {
            order.setReturnStatus("PICKUP_PENDING");
        } else {
            return ResponseEntity.ok("No status update needed at this time. Current return status: " + currentReturnStatus);
        }

        orderRepo.save(order);
        return ResponseEntity.ok("Return status auto-updated to: " + order.getReturnStatus());
    }

    @Transactional
    public ResponseEntity<?> replaceOrder(Integer orderId, String reason) {
        Order order = orderRepo.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.badRequest().body("Order not found with ID: " + orderId);
        }

        // Allow replacement only after delivery
        if (!"DELIVERED".equals(order.getDeliveryStatus())) {
            return ResponseEntity.badRequest().body("Replacement can only be requested after the order is delivered.");
        }

        // Check if order is already cancelled, returned, or already in replacement
        if (!"PLACED".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body("Cannot replace an order that is already cancelled, returned, or under replacement.");
        }

        // Update order for replacement
        order.setStatus("REPLACEMENT_REQUESTED");
        order.setReplacementStatus(true); // differentiate from return
        order.setReplacementReason(reason);
        order.setReturnRequestedAt(new Date()); // reused for timing auto-update

        orderRepo.save(order);

        return ResponseEntity.ok("Replacement request submitted successfully!");
    }
    
    @Transactional
    public ResponseEntity<?> updateReplacementStatus(Integer orderId, String newReplacementStatus) {
        Order order = orderRepo.findById(orderId).orElse(null);

        if (order == null) {
            return ResponseEntity.badRequest().body("Order not found with ID: " + orderId);
        }

        if (!Boolean.TRUE.equals(order.getReplacementStatus()) ||
            !"REPLACEMENT_REQUESTED".equals(order.getStatus())) {
            return ResponseEntity.badRequest().body("This order is not in the replacement process.");
        }

        if (order.getReturnRequestedAt() == null) {
            return ResponseEntity.badRequest().body("Replacement timestamp not found.");
        }

        String currentStatus = order.getReturnStatus();

        // ✅ Manual update
        if (newReplacementStatus != null && !newReplacementStatus.isEmpty()) {
            if ("DELIVERED".equalsIgnoreCase(currentStatus)) {
                return ResponseEntity.badRequest().body("Replacement already completed.");
            }

            order.setReturnStatus(newReplacementStatus.toUpperCase());

            if ("DELIVERED".equalsIgnoreCase(newReplacementStatus)) {
                order.setStatus("REPLACED");
                order.setReplacementStatus(false); // Mark process complete
            }

            orderRepo.save(order);
            return ResponseEntity.ok("Replacement status manually updated to: " + order.getReturnStatus());
        }

        // ✅ Automatic update
        long now = System.currentTimeMillis();
        long requestedTime = order.getReturnRequestedAt().getTime();
        long minutesPassed = (now - requestedTime) / (1000 * 60);

        if ("DELIVERED".equals(currentStatus)) {
            return ResponseEntity.badRequest().body("Replacement already completed.");
        }

        if (minutesPassed >= 6 && "OUT_FOR_DELIVERY".equals(currentStatus)) {
            order.setReturnStatus("DELIVERED");
            order.setStatus("REPLACED");
            order.setReplacementStatus(false);
        } else if (minutesPassed >= 5 && "SHIPPING".equals(currentStatus)) {
            order.setReturnStatus("OUT_FOR_DELIVERY");
        } else if (minutesPassed >= 4 && "PACKING".equals(currentStatus)) {
            order.setReturnStatus("SHIPPING");
        } else if (minutesPassed >= 3 && "PICKED_UP".equals(currentStatus)) {
            order.setReturnStatus("PACKING");
        } else if (minutesPassed >= 2 && "PICKUP_PENDING".equals(currentStatus)) {
            order.setReturnStatus("PICKED_UP");
        } else if (minutesPassed >= 1 && (currentStatus == null || "REPLACEMENT_REQUESTED".equals(currentStatus))) {
            order.setReturnStatus("PICKUP_PENDING");
        } else {
            return ResponseEntity.ok("No update needed at this time. Current status: " + currentStatus);
        }

        orderRepo.save(order);
        return ResponseEntity.ok("Replacement status auto-updated to: " + order.getReturnStatus());
    }

    
//  public ResponseEntity<?> getAllOrders() {
//  List<Order> orders = orderRepo.findAll();
//
//  if (orders.isEmpty()) {
//      return ResponseEntity.ok("No orders found.");
//  }
//
//  return ResponseEntity.ok(orders);
//}
//
//@Transactional
//public ResponseEntity<?> getOrdersByUser(Integer userId) {
//  List<Order> orders = orderRepo.findByUserId(userId);
//
//  if (orders.isEmpty()) {
//      return ResponseEntity.ok("No orders found for this user.");
//  }
//
//  return ResponseEntity.ok(orders);
//}

//    @Transactional
//    public ResponseEntity<?> updateReplacementStatus(Integer orderId) {
//        Order order = orderRepo.findById(orderId).orElse(null);
//
//        if (order == null) {
//            return ResponseEntity.badRequest().body("Order not found with ID: " + orderId);
//        }
//
//        if (!Boolean.TRUE.equals(order.getReplacementStatus()) ||
//            !"REPLACEMENT_REQUESTED".equals(order.getStatus())) {
//            return ResponseEntity.badRequest().body("This order is not in the replacement process.");
//        }
//
//        if (order.getReturnRequestedAt() == null) {
//            return ResponseEntity.badRequest().body("Replacement timestamp not found.");
//        }
//
//        long now = System.currentTimeMillis();
//        long requestedTime = order.getReturnRequestedAt().getTime();
//        long minutesPassed = (now - requestedTime) / (1000 * 60);
//
//        String status = order.getReturnStatus();
//
//        // Step 1: Pickup old item
//        if (minutesPassed >= 1 && (status == null || "REPLACEMENT_REQUESTED".equals(status))) {
//            order.setReturnStatus("PICKUP_PENDING");
//        } 
//        else if (minutesPassed >= 2 && "PICKUP_PENDING".equals(status)) {
//            order.setReturnStatus("PICKED_UP");
//        }
//
//        // Step 2: New item dispatch lifecycle
//        else if (minutesPassed >= 3 && "PICKED_UP".equals(status)) {
//            order.setReturnStatus("PACKING");
//        } 
//        else if (minutesPassed >= 4 && "PACKING".equals(status)) {
//            order.setReturnStatus("SHIPPING");
//        } 
//        else if (minutesPassed >= 5 && "SHIPPING".equals(status)) {
//            order.setReturnStatus("OUT_FOR_DELIVERY");
//        } 
//        else if (minutesPassed >= 6 && "OUT_FOR_DELIVERY".equals(status)) {
//            order.setReturnStatus("DELIVERED");
//            order.setStatus("REPLACED");
//            order.setReplacementStatus(false); // Done
//        } 
//        else if ("REPLACEMENT_DELIVERED".equals(status)) {
//            return ResponseEntity.badRequest().body("Replacement already completed.");
//        }
//
//        orderRepo.save(order);
//        return ResponseEntity.ok("Replacement status updated to: " + order.getReturnStatus());
//    }

    
    
//  @Transactional
//  public ResponseEntity<?> updateOrderDeliveryStatus(Integer orderId) {
//      Order order = orderRepo.findById(orderId).orElse(null);
//
//      if (order == null) {
//          return ResponseEntity.badRequest().body("Order not found with ID: " + orderId);
//      }
//
//      long now = System.currentTimeMillis();
//      long orderTime = order.getCreatedAt().getTime();
//      long minutesPassed = (now - orderTime) / (1000 * 60);
//
//      String currentStatus = order.getDeliveryStatus();
//
//      if ("DELIVERED".equals(currentStatus)) {
//          return ResponseEntity.badRequest().body("Order is already delivered. No more updates possible.");
//      }
//
//      else if (minutesPassed >= 1 && "PLACED".equals(currentStatus)) {
//          order.setDeliveryStatus("PACKING");
//      }
//
//      else if (minutesPassed >= 2 && "PACKING".equals(order.getDeliveryStatus())) {
//          order.setDeliveryStatus("SHIPPING");
//      }
//
//      else if (minutesPassed >= 3 && "SHIPPING".equals(order.getDeliveryStatus())) {
//          order.setDeliveryStatus("OUT_FOR_DELIVERY");
//      }
//
//      else if (minutesPassed >= 4 && "OUT_FOR_DELIVERY".equals(order.getDeliveryStatus())) {
//          order.setDeliveryStatus("DELIVERED");
//      }
//
//      orderRepo.save(order);
//      return ResponseEntity.ok("Order status updated to: " + order.getDeliveryStatus());
//  }


//    @Transactional
//  public ResponseEntity<?> updateReturnStatus(Integer orderId) {
//      Order order = orderRepo.findById(orderId).orElse(null);
//
//      if (order == null) {
//          return ResponseEntity.badRequest().body("Order not found with ID: " + orderId);
//      }
//
//      if (!"RETURN_REQUESTED".equals(order.getStatus())) {
//          return ResponseEntity.badRequest().body("This order is not in return process.");
//      }
//
//      long now = System.currentTimeMillis();
//      long returnRequestedTime = order.getReturnRequestedAt().getTime(); // You need a field for when return started
//      long minutesPassed = (now - returnRequestedTime) / (1000 * 60);
//      String currentReturnStatus = order.getReturnStatus();
//      
//      if (order.getReturnRequestedAt() == null) {
//          return ResponseEntity.badRequest().body("Return timestamp not found. Cannot update return status.");
//      }
//
//
//      else if (minutesPassed >= 1 && (currentReturnStatus == null || "RETURN_REQUESTED".equals(currentReturnStatus))) {
//          order.setReturnStatus("PICKUP_PENDING");
//      }
//      else if (minutesPassed >= 2 && "PICKUP_PENDING".equals(order.getReturnStatus())) {
//          order.setReturnStatus("PICKED_UP");
//      }
//      else if (minutesPassed >= 3 && "PICKED_UP".equals(order.getReturnStatus())) {
//          order.setReturnStatus("REFUND_INITIATED");
//      }
//      else if (minutesPassed >= 4 && "REFUND_INITIATED".equals(order.getReturnStatus())) {
//          order.setReturnStatus("REFUND_COMPLETED");
//          order.setStatus("RETURNED");
//      }
//      else if ("REFUND_COMPLETED".equals(order.getReturnStatus())) {
//          return ResponseEntity.badRequest().body("Return process already completed.");
//      }
//
//      orderRepo.save(order);
//      return ResponseEntity.ok("Return status updated to: " + order.getReturnStatus());
//  }
}

