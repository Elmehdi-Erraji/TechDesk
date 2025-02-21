package com.techdesk.services.Impl;

import com.techdesk.dto.AuthResponseDTO;
import com.techdesk.dto.LoginRequestDTO;
import com.techdesk.dto.RegisterRequestDTO;
import com.techdesk.dto.mappers.AppUserMapper;
import com.techdesk.entities.AppUser;
import com.techdesk.repositories.AppUserRepository;
import com.techdesk.services.AuthService;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;


@Service
public class AuthServiceImpl implements AuthService {

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public AuthServiceImpl(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        AppUser user = appUserRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return appUserMapper.toResponseDto(user);
    }

    @Override
    public AuthResponseDTO  register(RegisterRequestDTO registerRequest) {
        if (appUserRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }
        AppUser user = appUserMapper.toEntity(registerRequest);
        String hashedPassword = BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        AppUser savedUser = appUserRepository.save(user);
        return appUserMapper.toResponseDto(savedUser);
    }

}
