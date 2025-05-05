package com.maf.production.controller;

import com.maf.production.dto.ApiResponse;
import com.maf.production.dto.ProductDTO;
import com.maf.production.model.AvailabilityStatus;
import com.maf.production.model.ProductionType;
import com.maf.production.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success(product));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
            @RequestParam("name")           String name,
            @RequestParam("price")          BigDecimal price,
            @RequestParam("categoryName")   String categoryName,
            @RequestParam("subcategoryName")String subcategoryName,
            @RequestParam("articleNumber")  String articleNumber,
            @RequestParam("description")    String description,
            @RequestParam("dimensions")     String dimensions,
            @RequestParam("availability")   String availability,
            @RequestParam("productionType") String productionType,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        // Собираем DTO из параметров
        ProductDTO dto = new ProductDTO();
        dto.setName(name);
        dto.setPrice(price);
        dto.setCategoryName(categoryName);
        dto.setSubcategoryName(subcategoryName);
        dto.setArticleNumber(articleNumber);
        dto.setDescription(description);
        dto.setDimensions(dimensions);
        dto.setAvailability(AvailabilityStatus.valueOf(availability));
        dto.setProductionType(ProductionType.valueOf(productionType));

        // Вызываем сервис, передаём DTO и файл
        ProductDTO created = productService.create(dto, file);

        return ResponseEntity.ok(ApiResponse.success(created));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updatedProduct = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(ApiResponse.success("Продукт успешно обновлен", updatedProduct));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Продукт успешно удален"));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.getProductsByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/subcategory/{subcategoryId}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsBySubcategory(@PathVariable Long subcategoryId) {
        List<ProductDTO> products = productService.getProductsBySubcategory(subcategoryId);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/availability/{status}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByAvailability(@PathVariable AvailabilityStatus status) {
        List<ProductDTO> products = productService.getProductsByAvailability(status);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> searchProducts(@RequestParam String keyword) {
        List<ProductDTO> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success(products));
    }
}
