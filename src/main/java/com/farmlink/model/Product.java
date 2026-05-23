package com.farmlink.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @NotNull
    @DecimalMin("0.0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @NotNull
    @Min(0)
    private Integer quantityAvailable;

    private String unit;

    @Enumerated(EnumType.STRING)
    private Category category;

    private String imageUrl;

    private boolean available = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "farmer_id", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private User farmer;

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

    public enum Category {
        VEGETABLES, FRUITS, GRAINS, DAIRY, SPICES, PULSES, OTHER
    }

    public Product() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public Integer getQuantityAvailable() { return quantityAvailable; }
    public String getUnit() { return unit; }
    public Category getCategory() { return category; }
    public String getImageUrl() { return imageUrl; }
    public boolean isAvailable() { return available; }
    public User getFarmer() { return farmer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public void setQuantityAvailable(Integer qty) { this.quantityAvailable = qty; }
    public void setUnit(String unit) { this.unit = unit; }
    public void setCategory(Category category) { this.category = category; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setAvailable(boolean available) { this.available = available; }
    public void setFarmer(User farmer) { this.farmer = farmer; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private BigDecimal price;
        private Integer quantityAvailable;
        private String unit;
        private Category category;
        private String imageUrl;
        private boolean available = true;
        private User farmer;

        public Builder id(Long id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String d) { this.description = d; return this; }
        public Builder price(BigDecimal price) { this.price = price; return this; }
        public Builder quantityAvailable(Integer q) { this.quantityAvailable = q; return this; }
        public Builder unit(String unit) { this.unit = unit; return this; }
        public Builder category(Category c) { this.category = c; return this; }
        public Builder imageUrl(String url) { this.imageUrl = url; return this; }
        public Builder available(boolean a) { this.available = a; return this; }
        public Builder farmer(User farmer) { this.farmer = farmer; return this; }

        public Product build() {
            Product p = new Product();
            p.id = this.id;
            p.name = this.name;
            p.description = this.description;
            p.price = this.price;
            p.quantityAvailable = this.quantityAvailable;
            p.unit = this.unit;
            p.category = this.category;
            p.imageUrl = this.imageUrl;
            p.available = this.available;
            p.farmer = this.farmer;
            return p;
        }
    }
}
