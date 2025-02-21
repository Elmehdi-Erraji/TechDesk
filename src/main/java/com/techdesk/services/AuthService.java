package com.techdesk.services;

import com.techdesk.dto.AuthResponseDTO;
import com.techdesk.dto.LoginRequestDTO;
import com.techdesk.dto.RegisterRequestDTO;

/**
 * Service interface for authentication operations.
 * <p>
 * This interface provides methods for logging in and registering users.
 * Implementations should handle authentication, password encryption, and validation.
 * </p>
 */
public interface AuthService {

    /**
     * Authenticates a user based on the provided login credentials.
     *
     * @param loginRequest the login request containing username and password
     * @return an {@link AuthResponseDTO} containing authentication details if login is successful
     * @throws com.techdesk.web.errors.InvalidCredentialsException if the provided credentials are invalid
     */
    AuthResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * Registers a new user using the provided registration details.
     *
     * @param registerRequest the registration request containing user details
     * @return an {@link AuthResponseDTO} containing the details of the newly registered user
     * @throws com.techdesk.web.errors.UserAlreadyExistsException if a user with the given username already exists
     */
    AuthResponseDTO register(RegisterRequestDTO registerRequest);
}