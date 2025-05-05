package com.maf.production.service.impl;

import com.maf.production.dto.SubcategoryDTO;
import com.maf.production.exception.ResourceNotFoundException;
import com.maf.production.model.Category;
import com.maf.production.model.Subcategory;
import com.maf.production.repository.CategoryRepository;
import com.maf.production.repository.SubcategoryRepository;
import com.maf.production.service.SubcategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SubcategoryServiceImpl implements SubcategoryService {

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public List<SubcategoryDTO> getAllSubcategories() {
        return subcategoryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubcategoryDTO getSubcategoryById(Long id) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Подкатегория не найдена с ID: " + id));
        return convertToDTO(subcategory);
    }

    @Override
    public List<SubcategoryDTO> getSubcategoriesByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + categoryId));
        return subcategoryRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public SubcategoryDTO createSubcategory(SubcategoryDTO subcategoryDTO) {
        Subcategory subcategory = convertToEntity(subcategoryDTO);
        Subcategory savedSubcategory = subcategoryRepository.save(subcategory);
        return convertToDTO(savedSubcategory);
    }

    @Override
    public SubcategoryDTO updateSubcategory(Long id, SubcategoryDTO subcategoryDTO) {
        Subcategory existingSubcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Подкатегория не найдена с ID: " + id));

        existingSubcategory.setName(subcategoryDTO.getName());
        existingSubcategory.setDescription(subcategoryDTO.getDescription());

        if (!existingSubcategory.getCategory().getId().equals(subcategoryDTO.getCategoryId())) {
            Category category = categoryRepository.findById(subcategoryDTO.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + subcategoryDTO.getCategoryId()));
            existingSubcategory.setCategory(category);
        }

        Subcategory updatedSubcategory = subcategoryRepository.save(existingSubcategory);
        return convertToDTO(updatedSubcategory);
    }

    @Override
    public void deleteSubcategory(Long id) {
        Subcategory subcategory = subcategoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Подкатегория не найдена с ID: " + id));
        subcategoryRepository.delete(subcategory);
    }

    @Override
    public boolean existsByNameAndCategory(String name, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + categoryId));
        return subcategoryRepository.existsByNameAndCategory(name, category);
    }

    // Вспомогательные методы для конвертации Entity <-> DTO
    private SubcategoryDTO convertToDTO(Subcategory subcategory) {
        SubcategoryDTO dto = new SubcategoryDTO();
        dto.setId(subcategory.getId());
        dto.setName(subcategory.getName());
        dto.setDescription(subcategory.getDescription());

        if (subcategory.getCategory() != null) {
            dto.setCategoryId(subcategory.getCategory().getId());
            dto.setCategoryName(subcategory.getCategory().getName());
        }

        return dto;
    }

    private Subcategory convertToEntity(SubcategoryDTO dto) {
        Subcategory subcategory = new Subcategory();
        subcategory.setId(dto.getId());
        subcategory.setName(dto.getName());
        subcategory.setDescription(dto.getDescription());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + dto.getCategoryId()));
            subcategory.setCategory(category);
        }

        return subcategory;
    }
}