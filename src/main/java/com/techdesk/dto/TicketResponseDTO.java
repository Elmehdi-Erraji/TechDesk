package com.techdesk.dto;


import com.techdesk.entities.enums.TicketCategory;
import com.techdesk.entities.enums.TicketPriority;
import com.techdesk.entities.enums.TicketStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TicketResponseDTO {
    private UUID id;
    private String title;
    private String description;
    private TicketPriority priority;
    private TicketCategory category;
    private TicketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
