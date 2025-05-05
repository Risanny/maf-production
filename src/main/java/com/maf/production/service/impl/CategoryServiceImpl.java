package com.maf.production.service.impl;

import com.maf.production.dto.CategoryDTO;
import com.maf.production.dto.SubcategoryDTO;
import com.maf.production.exception.BusinessException;
import com.maf.production.exception.ResourceNotFoundException;
import com.maf.production.model.Category;
import com.maf.production.repository.CategoryRepository;
import com.maf.production.service.CategoryService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        List<Category> categories = categoryRepository.findAll();

        // Явная инициализация подкатегорий для каждой категории
        categories.forEach(category -> {
            Hibernate.initialize(category.getSubcategories());
        });

        return categories.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + id));

        // Явная инициализация подкатегорий
        Hibernate.initialize(category.getSubcategories());

        return convertToDTO(category);
    }

    @Override
    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        try {
            if (categoryRepository.existsByName(categoryDTO.getName())) {
                throw new BusinessException("Категория с названием '" + categoryDTO.getName() + "' уже существует");
            }

            Category category = convertToEntity(categoryDTO);
            Category savedCategory = categoryRepository.save(category);
            return convertToDTO(savedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new BusinessException("Ошибка при создании категории: возможно, категория с таким названием уже существует", e);
        }
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + id));

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());

        Category updatedCategory = categoryRepository.save(existingCategory);
        return convertToDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + id));
        categoryRepository.delete(category);
    }

    @Override
    public boolean existsByName(String name) {
        return categoryRepository.existsByName(name);
    }

    // Вспомогательные методы для конвертации Entity <-> DTO
    private CategoryDTO convertToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());

        // Конвертируем подкатегории
        if (category.getSubcategories() != null) {
            List<SubcategoryDTO> subcategoryDTOs = category.getSubcategories().stream()
                    .map(subcategory -> {
                        SubcategoryDTO subDto = new SubcategoryDTO();
                        subDto.setId(subcategory.getId());
                        subDto.setName(subcategory.getName());
                        subDto.setDescription(subcategory.getDescription());
                        subDto.setCategoryId(category.getId());
                        subDto.setCategoryName(category.getName());
                        return subDto;
                    })
                    .collect(Collectors.toList());
            dto.setSubcategories(subcategoryDTOs);
        }

        return dto;
    }

    private Category convertToEntity(CategoryDTO dto) {
        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        return category;
    }
}
