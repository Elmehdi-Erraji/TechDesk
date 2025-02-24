package com.techdesk.dto;

import com.techdesk.entities.enums.TicketStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateTicketStatusDTO {
    @NotNull(message = "Status is required")
    private TicketStatus status;
}
