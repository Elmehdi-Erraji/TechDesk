package com.techdesk.services;

import com.techdesk.entities.AppUser;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<AppUser> findById(UUID id);

}
