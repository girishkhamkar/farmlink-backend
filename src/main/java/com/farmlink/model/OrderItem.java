package com.farmlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "items", "customer", "farmer"})
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "farmer"})
    private Product product;

    private Integer quantity;

    @Column(precision = 10, scale = 2)
    private BigDecimal priceAtPurchase;

    public OrderItem() {}

    public Long getId() { return id; }
    public Order getOrder() { return order; }
    public Product getProduct() { return product; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }

    public void setId(Long id) { this.id = id; }
    public void setOrder(Order order) { this.order = order; }
    public void setProduct(Product product) { this.product = product; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public void setPriceAtPurchase(BigDecimal p) { this.priceAtPurchase = p; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private Order order;
        private Product product;
        private Integer quantity;
        private BigDecimal priceAtPurchase;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder order(Order o) { this.order = o; return this; }
        public Builder product(Product p) { this.product = p; return this; }
        public Builder quantity(Integer q) { this.quantity = q; return this; }
        public Builder priceAtPurchase(BigDecimal p) { this.priceAtPurchase = p; return this; }

        public OrderItem build() {
            OrderItem item = new OrderItem();
            item.id = this.id;
            item.order = this.order;
            item.product = this.product;
            item.quantity = this.quantity;
            item.priceAtPurchase = this.priceAtPurchase;
            return item;
        }
    }
}
