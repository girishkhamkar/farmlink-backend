package com.farmlink.dto;

import com.farmlink.model.User;
import jakarta.validation.constraints.*;
import lombok.Data;

// ---- Auth DTOs ----

@Data
class RegisterRequest {
    @NotBlank public String name;
    @Email @NotBlank public String email;
    @NotBlank @Size(min = 6) public String password;
    @NotNull public User.Role role;
    public String farmName;   // only for FARMER
    public String location;
    public String phone;
}

@Data
class LoginRequest {
    @Email @NotBlank public String email;
    @NotBlank public String password;
}

@Data
class AuthResponse {
    public String token;
    public String role;
    public String name;
    public Long userId;

    public AuthResponse(String token, String role, String name, Long userId) {
        this.token = token;
        this.role = role;
        this.name = name;
        this.userId = userId;
    }
}

// ---- Product DTOs ----

@Data
class ProductRequest {
    @NotBlank public String name;
    public String description;
    @NotNull @DecimalMin("0.0") public java.math.BigDecimal price;
    @NotNull @Min(0) public Integer quantityAvailable;
    public String unit;
    public com.farmlink.model.Product.Category category;
    public String imageUrl;
}

// ---- Order DTOs ----

@Data
class OrderItemRequest {
    @NotNull public Long productId;
    @NotNull @Min(1) public Integer quantity;
}

@Data
class OrderRequest {
    @NotEmpty public java.util.List<OrderItemRequest> items;
    @NotNull public Long farmerId;
    public String deliveryAddress;
}

// ---- Chat DTOs ----

@Data
class ChatMessage {
    public Long senderId;
    public Long receiverId;
    public String content;
    public String roomId;
    public String senderName;
    public java.time.LocalDateTime sentAt;
}
