package com.techdesk.dto;

import com.techdesk.entities.enums.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class AuthResponseDTO {
    private UUID id;
    private String username;
    private Role role;
}
