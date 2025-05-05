package com.maf.production.service.impl;

import com.maf.production.dto.ProductDTO;
import com.maf.production.exception.ResourceNotFoundException;
import com.maf.production.model.AvailabilityStatus;
import com.maf.production.model.Category;
import com.maf.production.model.Product;
import com.maf.production.model.Subcategory;
import com.maf.production.repository.CategoryRepository;
import com.maf.production.repository.ProductRepository;
import com.maf.production.repository.SubcategoryRepository;
import com.maf.production.service.ProductService;
import com.maf.production.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private FileUtil fileUtil;

    @Override
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден с ID: " + id));
        return convertToDTO(product);
    }

    // Новая реализация с обработкой MultipartFile
    @Override
    public ProductDTO create(ProductDTO dto, MultipartFile file) {
        // Собираем сущность из DTO
        Product product = convertToEntity(dto);

        // Сохраняем файл картинки, если передан
        if (file != null && !file.isEmpty()) {
            try {
                String filename = fileUtil.saveFile(file);
                product.setImageUrl(filename);
            } catch (Exception e) {
                throw new RuntimeException("Не удалось сохранить файл изображения", e);
            }
        }

        // Сохраняем продукт и возвращаем DTO
        Product saved = productRepository.save(product);
        return convertToDTO(saved);
    }

    @Override
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден с ID: " + id));

        // Обновляем все поля продукта
        existingProduct.setName(productDTO.getName());
        existingProduct.setDescription(productDTO.getDescription());
        existingProduct.setArticleNumber(productDTO.getArticleNumber());
        existingProduct.setDimensions(productDTO.getDimensions());
        existingProduct.setPrice(productDTO.getPrice());
        existingProduct.setProductionType(productDTO.getProductionType());
        existingProduct.setImageUrl(productDTO.getImageUrl());
        existingProduct.setAvailability(productDTO.getAvailability());

        // Устанавливаем категорию и подкатегорию
        Category category = categoryRepository.findById(productDTO.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + productDTO.getCategoryId()));
        existingProduct.setCategory(category);

        if (productDTO.getSubcategoryId() != null) {
            Subcategory subcategory = subcategoryRepository.findById(productDTO.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Подкатегория не найдена с ID: " + productDTO.getSubcategoryId()));
            existingProduct.setSubcategory(subcategory);
        } else {
            existingProduct.setSubcategory(null);
        }

        Product updatedProduct = productRepository.save(existingProduct);
        return convertToDTO(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Продукт не найден с ID: " + id));
        productRepository.delete(product);
    }

    @Override
    public List<ProductDTO> getProductsByCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + categoryId));
        return productRepository.findByCategory(category).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsBySubcategory(Long subcategoryId) {
        Subcategory subcategory = subcategoryRepository.findById(subcategoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Подкатегория не найдена с ID: " + subcategoryId));
        return productRepository.findBySubcategory(subcategory).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> getProductsByAvailability(AvailabilityStatus status) {
        return productRepository.findByAvailability(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProductDTO> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Вспомогательные методы для конвертации Entity <-> DTO
    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setArticleNumber(product.getArticleNumber());
        dto.setDimensions(product.getDimensions());
        dto.setPrice(product.getPrice());
        dto.setProductionType(product.getProductionType());
        dto.setImageUrl(product.getImageUrl());
        dto.setAvailability(product.getAvailability());

        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }

        if (product.getSubcategory() != null) {
            dto.setSubcategoryId(product.getSubcategory().getId());
            dto.setSubcategoryName(product.getSubcategory().getName());
        }

        return dto;
    }

    private Product convertToEntity(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setDescription(dto.getDescription());
        product.setArticleNumber(dto.getArticleNumber());
        product.setDimensions(dto.getDimensions());
        product.setPrice(dto.getPrice());
        product.setProductionType(dto.getProductionType());
        product.setImageUrl(dto.getImageUrl());
        product.setAvailability(dto.getAvailability());

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Категория не найдена с ID: " + dto.getCategoryId()));
            product.setCategory(category);
        }

        if (dto.getSubcategoryId() != null) {
            Subcategory subcategory = subcategoryRepository.findById(dto.getSubcategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Подкатегория не найдена с ID: " + dto.getSubcategoryId()));
            product.setSubcategory(subcategory);
        }

        return product;
    }
}
