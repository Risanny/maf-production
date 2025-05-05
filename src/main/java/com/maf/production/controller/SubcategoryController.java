package com.maf.production.controller;

import com.maf.production.dto.ApiResponse;
import com.maf.production.dto.SubcategoryDTO;
import com.maf.production.service.SubcategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/subcategories")
public class SubcategoryController {
    @Autowired
    private SubcategoryService subcategoryService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SubcategoryDTO>>> getAllSubcategories() {
        List<SubcategoryDTO> subcategories = subcategoryService.getAllSubcategories();
        return ResponseEntity.ok(ApiResponse.success(subcategories));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SubcategoryDTO>> getSubcategoryById(@PathVariable Long id) {
        SubcategoryDTO subcategory = subcategoryService.getSubcategoryById(id);
        return ResponseEntity.ok(ApiResponse.success(subcategory));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<SubcategoryDTO>>> getSubcategoriesByCategory(@PathVariable Long categoryId) {
        List<SubcategoryDTO> subcategories = subcategoryService.getSubcategoriesByCategory(categoryId);
        return ResponseEntity.ok(ApiResponse.success(subcategories));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<SubcategoryDTO>> createSubcategory(@Valid @RequestBody SubcategoryDTO subcategoryDTO) {
        if (subcategoryService.existsByNameAndCategory(subcategoryDTO.getName(), subcategoryDTO.getCategoryId())) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.<SubcategoryDTO>error("Подкатегория с названием '" + subcategoryDTO.getName() +
                            "' уже существует в данной категории")
            );
        }

        SubcategoryDTO createdSubcategory = subcategoryService.createSubcategory(subcategoryDTO);
        return ResponseEntity.ok(ApiResponse.success("Подкатегория успешно создана", createdSubcategory));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public ResponseEntity<ApiResponse<SubcategoryDTO>> updateSubcategory(@PathVariable Long id,
                                                                         @Valid @RequestBody SubcategoryDTO subcategoryDTO) {
        SubcategoryDTO updatedSubcategory = subcategoryService.updateSubcategory(id, subcategoryDTO);
        return ResponseEntity.ok(ApiResponse.success("Подкатегория успешно обновлена", updatedSubcategory));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<?>> deleteSubcategory(@PathVariable Long id) {
        subcategoryService.deleteSubcategory(id);
        return ResponseEntity.ok(ApiResponse.success("Подкатегория успешно удалена"));
    }
}