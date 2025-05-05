package com.maf.production.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/**
 * DTO для оформления заказа из каталога МАФ.
 */
@Getter
@Setter
@NoArgsConstructor
public class OrderRequest {

    @NotBlank(message = "Имя обязательно")
    private String name;

    @NotBlank(message = "Телефон обязателен")
    private String phone;

    @Email(message = "Некорректный формат e-mail")
    private String email;

    private String comment;

    @NotEmpty(message = "Список товаров не может быть пустым")
    private List<OrderItem> items;

}
