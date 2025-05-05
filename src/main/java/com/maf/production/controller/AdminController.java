package com.maf.production.controller;

import com.maf.production.dto.ApiResponse;
import com.maf.production.dto.ProductDTO;
import com.maf.production.dto.UserDTO;
import com.maf.production.service.ProductService;
import com.maf.production.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardData() {
        List<UserDTO> users = userService.getAllUsers();
        List<ProductDTO> products = productService.getAllProducts();

        Map<String, Object> dashboardData = new HashMap<>();
        dashboardData.put("totalUsers", users.size());
        dashboardData.put("totalProducts", products.size());

        // Здесь можно добавить другую статистику для административной панели

        return ResponseEntity.ok(ApiResponse.success(dashboardData));
    }

    // Другие административные функции можно добавить здесь
}