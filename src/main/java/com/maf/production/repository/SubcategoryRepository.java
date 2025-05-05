package com.maf.production.repository;

import com.maf.production.model.Category;
import com.maf.production.model.Subcategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    List<Subcategory> findByCategory(Category category);
    Optional<Subcategory> findByNameAndCategory(String name, Category category);
    boolean existsByNameAndCategory(String name, Category category);
}
