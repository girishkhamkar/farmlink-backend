package com.farmlink.service;

import com.farmlink.model.Product;
import com.farmlink.model.User;
import com.farmlink.repository.ProductRepository;
import com.farmlink.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    // Manual constructor — replaces @RequiredArgsConstructor
    public ProductService(ProductRepository productRepository,
                          UserRepository userRepository) {
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    // Farmer: create a product
    public Product createProduct(String farmerEmail, String name, String description,
                                  BigDecimal price, Integer quantity, String unit,
                                  Product.Category category, String imageUrl) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));

        Product product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .quantityAvailable(quantity)
                .unit(unit)
                .category(category)
                .imageUrl(imageUrl)
                .farmer(farmer)
                .available(true)
                .build();

        return productRepository.save(product);
    }

    // Farmer: update their product
    public Product updateProduct(Long productId, String farmerEmail, String name,
                                  String description, BigDecimal price,
                                  Integer quantity, boolean available) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getFarmer().getEmail().equals(farmerEmail)) {
            throw new RuntimeException("You can only edit your own products");
        }

        product.setName(name);
        product.setDescription(description);
        product.setPrice(price);
        product.setQuantityAvailable(quantity);
        product.setAvailable(available);

        return productRepository.save(product);
    }

    // Farmer: delete their product
    public void deleteProduct(Long productId, String farmerEmail) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (!product.getFarmer().getEmail().equals(farmerEmail)) {
            throw new RuntimeException("You can only delete your own products");
        }

        productRepository.delete(product);
    }

    // Farmer: see their own listings
    public List<Product> getMyProducts(String farmerEmail) {
        User farmer = userRepository.findByEmail(farmerEmail)
                .orElseThrow(() -> new RuntimeException("Farmer not found"));
        return productRepository.findByFarmer(farmer);
    }

    // Public: browse all available products
    public List<Product> getAllAvailable() {
        return productRepository.findByAvailableTrue();
    }

    // Public: browse by category
    public List<Product> getByCategory(Product.Category category) {
        return productRepository.findByCategoryAndAvailableTrue(category);
    }

    // Public: search by name
    public List<Product> search(String keyword) {
        return productRepository.findByNameContainingIgnoreCaseAndAvailableTrue(keyword);
    }

    // Get single product
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }
}