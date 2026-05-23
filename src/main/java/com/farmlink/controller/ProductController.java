package com.farmlink.controller;

import com.farmlink.model.Product;
import com.farmlink.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProductController {

    private final ProductService productService;

    // Manual constructor — replaces @RequiredArgsConstructor
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // PUBLIC - anyone can browse
    @GetMapping("/products/browse")
    public ResponseEntity<List<Product>> browse(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String category) {

        if (search != null && !search.isEmpty()) {
            return ResponseEntity.ok(productService.search(search));
        }
        if (category != null && !category.isEmpty()) {
            return ResponseEntity.ok(productService.getByCategory(
                    Product.Category.valueOf(category.toUpperCase())));
        }
        return ResponseEntity.ok(productService.getAllAvailable());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<?> getProduct(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(productService.getById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    // FARMER ONLY - manage their products
    @PreAuthorize("hasRole('FARMER')")
    @PostMapping("/farmer/products")
    public ResponseEntity<?> createProduct(@RequestBody Map<String, Object> body,
                                            Authentication auth) {
        try {
            Product product = productService.createProduct(
                    auth.getName(),
                    (String) body.get("name"),
                    (String) body.get("description"),
                    new BigDecimal(body.get("price").toString()),
                    Integer.valueOf(body.get("quantityAvailable").toString()),
                    (String) body.get("unit"),
                    body.get("category") != null ?
                            Product.Category.valueOf(body.get("category").toString()) : null,
                    (String) body.get("imageUrl")
            );
            return ResponseEntity.ok(product);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('FARMER')")
    @GetMapping("/farmer/products")
    public ResponseEntity<List<Product>> myProducts(Authentication auth) {
        return ResponseEntity.ok(productService.getMyProducts(auth.getName()));
    }

    @PreAuthorize("hasRole('FARMER')")
    @PutMapping("/farmer/products/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id,
                                            @RequestBody Map<String, Object> body,
                                            Authentication auth) {
        try {
            Product updated = productService.updateProduct(
                    id,
                    auth.getName(),
                    (String) body.get("name"),
                    (String) body.get("description"),
                    new BigDecimal(body.get("price").toString()),
                    Integer.valueOf(body.get("quantityAvailable").toString()),
                    Boolean.valueOf(body.get("available").toString())
            );
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('FARMER')")
    @DeleteMapping("/farmer/products/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication auth) {
        try {
            productService.deleteProduct(id, auth.getName());
            return ResponseEntity.ok(Map.of("message", "Product deleted"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}