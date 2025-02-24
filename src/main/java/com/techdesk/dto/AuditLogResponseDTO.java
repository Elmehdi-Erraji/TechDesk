package com.techdesk.dto;

import com.techdesk.entities.enums.AuditLogType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class AuditLogResponseDTO {
    private UUID id;
    private UUID ticketId;
    private String changedByUsername;
    private AuditLogType logType;
    private String description;
    private LocalDateTime timestamp;
}
