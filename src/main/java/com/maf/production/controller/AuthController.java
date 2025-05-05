package com.maf.production.controller;

import com.maf.production.dto.ApiResponse;
import com.maf.production.dto.AuthRequest;
import com.maf.production.dto.AuthResponse;
import com.maf.production.dto.SignupRequest;
import com.maf.production.dto.UserDTO;
import com.maf.production.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<AuthResponse>> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
        AuthResponse response = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Аутентификация успешна", response));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        UserDTO user = authService.registerUser(signUpRequest);
        return ResponseEntity.ok(ApiResponse.success("Пользователь успешно зарегистрирован", user));
    }
}
