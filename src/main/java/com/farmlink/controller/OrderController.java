package com.farmlink.controller;

import com.farmlink.model.Order;
import com.farmlink.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {

    private final OrderService orderService;

    // Manual constructor — replaces @RequiredArgsConstructor
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // CUSTOMER: place an order
    @PreAuthorize("hasRole('CUSTOMER')")
    @PostMapping("/customer/orders")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> body,
                                         Authentication auth) {
        try {
            Long farmerId = Long.valueOf(body.get("farmerId").toString());
            List<Map<String, Object>> items = (List<Map<String, Object>>) body.get("items");
            String deliveryAddress = (String) body.get("deliveryAddress");

            Order order = orderService.placeOrder(auth.getName(), farmerId,
                    items, deliveryAddress);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // CUSTOMER: view their orders
    @PreAuthorize("hasRole('CUSTOMER')")
    @GetMapping("/customer/orders")
    public ResponseEntity<List<Order>> myOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getCustomerOrders(auth.getName()));
    }

    // FARMER: view orders they received
    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/farmer/orders")
    public ResponseEntity<List<Order>> farmerOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.getFarmerOrders(auth.getName()));
    }

    // FARMER: update order status
    @PreAuthorize("hasRole('FARMER')")
    @PatchMapping("/farmer/orders/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                           @RequestBody Map<String, String> body,
                                           Authentication auth) {
        try {
            Order.OrderStatus newStatus = Order.OrderStatus.valueOf(body.get("status"));
            Order updated = orderService.updateOrderStatus(id, auth.getName(), newStatus);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ADMIN: view all orders
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/orders")
    public ResponseEntity<List<Order>> allOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
}