package com.example.ecpro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.ecpro.model.OrderRequest;
import com.example.ecpro.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins= "*")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @PostMapping("/placeOrder")
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request) {
        return orderService.placeOrder(request);
    }

    @PostMapping("/BuyNow")
    public ResponseEntity<?> BuyNow(@RequestBody OrderRequest request,
                                    @RequestParam Integer productId,
                                    @RequestParam Integer quantity) {
        return orderService.BuyNow(request, productId, quantity);
    }

    
    @GetMapping("/getAllOrders")
    public ResponseEntity<?> getAllorders(){
    	return orderService.getAllOrders();
    }
    
    @GetMapping("/getOrdersByUser/{userId}")
    public ResponseEntity<?> getOrdersByUser(@PathVariable Integer userId) {
        return orderService.getOrdersByUser(userId);
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Integer orderId,
            @RequestParam String reason) {
        return orderService.cancelOrder(orderId, reason);
    }
    
    @PutMapping("/updateDelivery/{orderId}")
    public ResponseEntity<?> updateOrderDeliveryStatus(@PathVariable Integer orderId,@RequestParam String newStatus){
    	return orderService.updateOrderDeliveryStatus(orderId, newStatus);
    }
   
    @PutMapping("/return/{orderId}")
    public ResponseEntity<?> returnOrder(@PathVariable Integer orderId, @RequestParam String reason) {
        return orderService.returnOrder(orderId, reason);
    }
    
    @PutMapping("/ReturnStatus/{orderId}")
    public ResponseEntity<?> updateReturnStatus(@PathVariable Integer orderId,@RequestParam String newReturnStatus) {
        return orderService.updateReturnStatus(orderId, newReturnStatus);
    }

     @PutMapping("/replace/{orderId}")
    public ResponseEntity<?> replaceOrder(@PathVariable Integer orderId, @RequestParam String reason) {
        return orderService.replaceOrder(orderId, reason);
    }
     
     @PutMapping("/updateReplacementStatus/{orderId}")
     public ResponseEntity<?> updateReplacementStatus(@PathVariable Integer orderId,@RequestParam String newReplacementStatus) {
         return orderService.updateReplacementStatus(orderId, newReplacementStatus);
     }
     
//   @PutMapping("/updateDelivery/{orderId}")
//   public ResponseEntity<?> updateDeliveryStatus(@PathVariable Integer orderId) {
//       return orderService.updateOrderDeliveryStatus(orderId);
//   }

}

