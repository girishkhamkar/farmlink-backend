package com.farmlink.repository;

import com.farmlink.model.Product;
import com.farmlink.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByFarmer(User farmer);
    List<Product> findByAvailableTrue();
    List<Product> findByCategoryAndAvailableTrue(Product.Category category);
    List<Product> findByNameContainingIgnoreCaseAndAvailableTrue(String name);
}