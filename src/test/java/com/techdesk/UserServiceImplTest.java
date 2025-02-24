package com.techdesk;

import com.techdesk.entities.AppUser;
import com.techdesk.repositories.AppUserRepository;
import com.techdesk.services.Impl.UserServiceImpl;
import com.techdesk.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UUID userId;
    private AppUser appUser;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        appUser = new AppUser();
        appUser.setId(userId);
        appUser.setUsername("testUser");
    }

    @Test
    void findById_UserFound_ReturnsUser() {
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(appUser));

        Optional<AppUser> result = userService.findById(userId);

        assertTrue(result.isPresent(), "User should be found");
        assertEquals(appUser, result.get(), "Returned user should match the expected user");
        verify(appUserRepository, times(1)).findById(userId);
    }

    @Test
    void findById_UserNotFound_ReturnsEmptyOptional() {
        when(appUserRepository.findById(userId)).thenReturn(Optional.empty());

        Optional<AppUser> result = userService.findById(userId);

        assertFalse(result.isPresent(), "User should not be found");
        verify(appUserRepository, times(1)).findById(userId);
    }


    @Test
    void findById_RepositoryThrowsException_PropagatesException() {
        when(appUserRepository.findById(userId)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(DataAccessException.class, () -> userService.findById(userId),
                "Repository exception should be propagated");
        verify(appUserRepository, times(1)).findById(userId);
    }

    @Test
    void findById_TransactionalReadOnly() {
        when(appUserRepository.findById(userId)).thenReturn(Optional.of(appUser));

        Optional<AppUser> result = userService.findById(userId);
        assertTrue(result.isPresent(), "User should be found");
        verify(appUserRepository, times(1)).findById(userId);

    }
}