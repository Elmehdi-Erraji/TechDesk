package com.techdesk.services;

import com.techdesk.dto.AuthResponseDTO;
import com.techdesk.dto.LoginRequestDTO;
import com.techdesk.dto.RegisterRequestDTO;

public interface AuthService {
    AuthResponseDTO login(LoginRequestDTO loginRequest);
    AuthResponseDTO register(RegisterRequestDTO registerRequest);
}
