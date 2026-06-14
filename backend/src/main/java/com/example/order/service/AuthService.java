package com.example.order.service;

import com.example.order.dto.LoginRequest;
import com.example.order.dto.LoginResponse;

public interface AuthService {

    LoginResponse login(LoginRequest request);
}
