package com.maf.production.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubcategoryDTO {
    private Long id;

    @NotBlank(message = "Название подкатегории обязательно")
    private String name;

    private String description;

    @NotNull(message = "ID категории обязательно")
    private Long categoryId;

    private String categoryName;
}
