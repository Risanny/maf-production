package com.maf.production.service;

import com.maf.production.dto.SubcategoryDTO;

import java.util.List;

public interface SubcategoryService {
    List<SubcategoryDTO> getAllSubcategories();
    SubcategoryDTO getSubcategoryById(Long id);
    List<SubcategoryDTO> getSubcategoriesByCategory(Long categoryId);
    SubcategoryDTO createSubcategory(SubcategoryDTO subcategoryDTO);
    SubcategoryDTO updateSubcategory(Long id, SubcategoryDTO subcategoryDTO);
    void deleteSubcategory(Long id);
    boolean existsByNameAndCategory(String name, Long categoryId);
}
