package com.maf.production.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maf.production.dto.ApiResponse;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/public")
    public ApiResponse<?> publicAccess() {
        return ApiResponse.success("Публичный доступ работает!");
    }

    @GetMapping("/auth")
    public ApiResponse<?> authenticatedAccess() {
        return ApiResponse.success("Аутентифицированный доступ работает!");
    }
}
