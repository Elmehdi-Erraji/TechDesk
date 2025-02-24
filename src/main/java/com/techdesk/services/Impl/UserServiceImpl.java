package com.techdesk.services.Impl;


import com.techdesk.entities.AppUser;
import com.techdesk.repositories.AppUserRepository;
import com.techdesk.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of the {@link UserService} interface.
 * <p>
 * This class provides methods to retrieve {@link AppUser} entities using the {@link AppUserRepository}.
 * </p>
 */
@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;

    /**
     * Constructs a {@code UserServiceImpl} with the required {@link AppUserRepository}.
     *
     * @param appUserRepository the repository used to perform user operations
     */
    public UserServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findById(UUID id) {
        return appUserRepository.findById(id);
    }
}
