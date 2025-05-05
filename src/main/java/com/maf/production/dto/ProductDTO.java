package com.maf.production.dto;

import com.maf.production.model.AvailabilityStatus;
import com.maf.production.model.ProductionType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {
    private Long id;

    @NotBlank(message = "Наименование обязательно")
    private String name;

    private String description;

    @NotBlank(message = "Артикул обязателен")
    private String articleNumber;

    private String dimensions;

    @NotNull(message = "Цена обязательна")
    @Positive(message = "Цена должна быть положительной")
    private BigDecimal price;

    @NotNull(message = "Тип производства обязателен")
    private ProductionType productionType;

    @NotNull(message = "Категория обязательна")
    private Long categoryId;

    private String categoryName;

    private Long subcategoryId;

    private String subcategoryName;

    private String imageUrl;

    @NotNull(message = "Статус наличия обязателен")
    private AvailabilityStatus availability;
}
