package com.techdesk.repositories;

import com.techdesk.entities.AppUser;
import com.techdesk.entities.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {
    Optional<AppUser> findByUsername(String username);
    List<AppUser> findByRole(Role role);

}
