package com.techdesk.services;

import com.techdesk.entities.AppUser;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing AppUser operations.
 * <p>
 * This interface provides methods for retrieving user information. Implementations should handle
 * fetching user data from the underlying data store.
 * </p>
 */
public interface UserService {

    /**
     * Finds an {@link AppUser} by its unique identifier.
     *
     * @param id the unique identifier of the user
     * @return an {@link Optional} containing the found {@link AppUser} if present, or an empty {@link Optional} otherwise
     */
    Optional<AppUser> findById(UUID id);
}