package com.techdesk.services.Impl;

import com.techdesk.dto.AuthResponseDTO;
import com.techdesk.dto.LoginRequestDTO;
import com.techdesk.dto.RegisterRequestDTO;
import com.techdesk.dto.mappers.AppUserMapper;
import com.techdesk.entities.AppUser;
import com.techdesk.repositories.AppUserRepository;
import com.techdesk.services.AuthService;
import com.techdesk.web.errors.InvalidCredentialsException;
import com.techdesk.web.errors.UserAlreadyExistsException;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AppUserRepository appUserRepository;
    private final AppUserMapper appUserMapper;

    public AuthServiceImpl(AppUserRepository appUserRepository, AppUserMapper appUserMapper) {
        this.appUserRepository = appUserRepository;
        this.appUserMapper = appUserMapper;
    }

    @Override
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        AppUser user = appUserRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> {
                    logger.warn("Login failed: Invalid credentials for username '{}'", loginRequest.getUsername());
                    return new InvalidCredentialsException("Invalid credentials");
                });
        if (!BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            logger.warn("Login failed: Invalid credentials for username '{}'", loginRequest.getUsername());
            throw new InvalidCredentialsException("Invalid credentials");
        }
        logger.info("User '{}' logged in successfully", loginRequest.getUsername());
        return appUserMapper.toResponseDto(user);
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO registerRequest) {
        if (appUserRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            logger.warn("Registration failed: Username '{}' already exists", registerRequest.getUsername());
            throw new UserAlreadyExistsException("Username already exists");
        }
        AppUser user = appUserMapper.toEntity(registerRequest);
        String hashedPassword = BCrypt.hashpw(registerRequest.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        AppUser savedUser = appUserRepository.save(user);
        logger.info("User '{}' registered successfully", registerRequest.getUsername());
        return appUserMapper.toResponseDto(savedUser);
    }
}
