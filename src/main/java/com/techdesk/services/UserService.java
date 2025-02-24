package com.techdesk.services;

import com.techdesk.entities.AppUser;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing AppUser operations.
 *
 * This interface provides methods for retrieving user information. Implementations should handle
 * fetching user data from the underlying data store.
 */
public interface UserService {

    /**
     * Finds an AppUser by its unique identifier.
     *
     * @param id the unique identifier of the user
     * @return an Optional containing the found AppUser if present, or an empty Optional otherwise
     */
    Optional<AppUser> findById(UUID id);
}
