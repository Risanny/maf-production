package com.maf.production.service;

import com.maf.production.dto.ProductDTO;
import com.maf.production.model.AvailabilityStatus;

import java.util.List;

public interface ProductService {
    List<ProductDTO> getAllProducts();
    ProductDTO getProductById(Long id);
    ProductDTO createProduct(ProductDTO productDTO);
    ProductDTO updateProduct(Long id, ProductDTO productDTO);
    void deleteProduct(Long id);
    List<ProductDTO> getProductsByCategory(Long categoryId);
    List<ProductDTO> getProductsBySubcategory(Long subcategoryId);
    List<ProductDTO> getProductsByAvailability(AvailabilityStatus status);
    List<ProductDTO> searchProducts(String keyword);
}
