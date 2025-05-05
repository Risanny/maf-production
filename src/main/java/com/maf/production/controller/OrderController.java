package com.maf.production.controller;

import com.maf.production.dto.ApiResponse;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import com.maf.production.dto.OrderRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${app.maf.company.email}")
    private String companyEmail;

    @PostMapping
    public ResponseEntity<ApiResponse<Void>> placeOrder(@Validated @RequestBody OrderRequest req) {
        // Собираем письмо
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(companyEmail); // ваш адрес
        msg.setSubject("Новый заказ из каталога МАФ");
        StringBuilder text = new StringBuilder();
        text.append("Имя: ").append(req.getName()).append("\n");
        text.append("Телефон: ").append(req.getPhone()).append("\n");
        text.append("E-mail: ").append(req.getEmail()).append("\n");
        text.append("Комментарий: ").append(req.getComment()).append("\n\n");
        text.append("Товары:\n");
        req.getItems().forEach(i ->
                text.append("• ").append(i.getName())
                        .append(" (ID=").append(i.getId()).append(") — ")
                        .append(i.getPrice()).append(" ₸\n")
        );
        msg.setText(text.toString());
        mailSender.send(msg);

        return ResponseEntity.ok(ApiResponse.success("Заказ принят и отправлен"));
    }
}
