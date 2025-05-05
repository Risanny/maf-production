package com.maf.production.service;

import com.maf.production.dto.AuthRequest;
import com.maf.production.dto.AuthResponse;
import com.maf.production.dto.SignupRequest;
import com.maf.production.dto.UserDTO;

public interface AuthService {
    AuthResponse authenticateUser(AuthRequest loginRequest);
    UserDTO registerUser(SignupRequest signupRequest);
}
