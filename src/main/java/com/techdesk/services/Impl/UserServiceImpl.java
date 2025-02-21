package com.techdesk.services.Impl;


import com.techdesk.entities.AppUser;
import com.techdesk.repositories.AppUserRepository;
import com.techdesk.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final AppUserRepository appUserRepository;

    public UserServiceImpl(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AppUser> findById(UUID id) {
        return appUserRepository.findById(id);
    }
}
