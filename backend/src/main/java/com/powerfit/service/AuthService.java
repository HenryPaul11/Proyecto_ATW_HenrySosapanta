package com.powerfit.service;

import com.powerfit.dto.request.LoginRequest;
import com.powerfit.dto.response.LoginResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
}
