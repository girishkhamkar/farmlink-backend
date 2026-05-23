package com.farmlink.service;

import com.farmlink.model.*;
import com.farmlink.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Manual constructor — replaces @RequiredArgsConstructor
    public OrderService(OrderRepository orderRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order placeOrder(String customerEmail, Long farmerId,
                             List<Map<String, Object>> items, String deliveryAddress) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        User farmer = userRepository.findById(farmerId)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Order order = Order.builder()
                .customer(customer)
                .farmer(farmer)
                .status(Order.OrderStatus.PLACED)
                .deliveryAddress(deliveryAddress)
                .totalAmount(BigDecimal.ZERO)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (Map<String, Object> item : items) {
            Long productId = Long.valueOf(item.get("productId").toString());
            Integer qty = Integer.valueOf(item.get("quantity").toString());

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Product not found: " + productId));

            if (product.getQuantityAvailable() < qty) {
                throw new RuntimeException("Insufficient stock for: " + product.getName());
            }

            // Deduct stock
            product.setQuantityAvailable(product.getQuantityAvailable() - qty);
            productRepository.save(product);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(qty)
                    .priceAtPurchase(product.getPrice())
                    .build();

            order.getItems().add(orderItem);
            total = total.add(product.getPrice().multiply(BigDecimal.valueOf(qty)));
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    // Customer: see their orders
    public List<Order> getCustomerOrders(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer);
    }

    // Farmer: see orders for them
    public List<Order> getFarmerOrders(String farmerEmail) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return orderRepository.findByFarmerOrderByCreatedAtDesc(farmer);
    }

    // Farmer updates order status
    public Order updateOrderStatus(Long orderId, String farmerEmail, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getFarmer().getEmail().equals(farmerEmail)) {
            throw new RuntimeException("Not your order to update");
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    // Admin: all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}