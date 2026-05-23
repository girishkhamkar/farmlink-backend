package com.farmlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private User farmer;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"order"})
    private List<OrderItem> items = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PLACED;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    private String deliveryAddress;
    private String paymentStatus = "PENDING";

    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum OrderStatus {
        PLACED, CONFIRMED, PACKED, SHIPPED, DELIVERED, CANCELLED
    }

    public Order() {}

    public Long getId() { return id; }
    public User getCustomer() { return customer; }
    public User getFarmer() { return farmer; }
    public List<OrderItem> getItems() { return items; }
    public OrderStatus getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getPaymentStatus() { return paymentStatus; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setCustomer(User customer) { this.customer = customer; }
    public void setFarmer(User farmer) { this.farmer = farmer; }
    public void setItems(List<OrderItem> items) { this.items = items; }
    public void setStatus(OrderStatus status) { this.status = status; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private User customer;
        private User farmer;
        private List<OrderItem> items = new ArrayList<>();
        private OrderStatus status = OrderStatus.PLACED;
        private BigDecimal totalAmount;
        private String deliveryAddress;
        private String paymentStatus = "PENDING";

        public Builder id(Long id) { this.id = id; return this; }
        public Builder customer(User c) { this.customer = c; return this; }
        public Builder farmer(User f) { this.farmer = f; return this; }
        public Builder items(List<OrderItem> i) { this.items = i; return this; }
        public Builder status(OrderStatus s) { this.status = s; return this; }
        public Builder totalAmount(BigDecimal t) { this.totalAmount = t; return this; }
        public Builder deliveryAddress(String d) { this.deliveryAddress = d; return this; }
        public Builder paymentStatus(String p) { this.paymentStatus = p; return this; }

        public Order build() {
            Order o = new Order();
            o.id = this.id;
            o.customer = this.customer;
            o.farmer = this.farmer;
            o.items = this.items;
            o.status = this.status;
            o.totalAmount = this.totalAmount;
            o.deliveryAddress = this.deliveryAddress;
            o.paymentStatus = this.paymentStatus;
            return o;
        }
    }
}
