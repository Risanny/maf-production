package com.maf.production.repository;

import com.maf.production.model.AvailabilityStatus;
import com.maf.production.model.Category;
import com.maf.production.model.Product;
import com.maf.production.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory(Category category);
    List<Product> findBySubcategory(Subcategory subcategory);
    List<Product> findByAvailability(AvailabilityStatus status);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByArticleNumber(String articleNumber);
}