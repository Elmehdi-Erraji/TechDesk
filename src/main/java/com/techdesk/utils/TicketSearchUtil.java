package com.techdesk.utils;

import com.techdesk.entities.enums.TicketStatus;

import java.util.Optional;
import java.util.UUID;

public class TicketSearchUtil {

    public static Optional<UUID> parseUuid(String ticketId) {
        if (ticketId == null || ticketId.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(UUID.fromString(ticketId));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid ticketId format");
        }
    }

    public static Optional<TicketStatus> parseTicketStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            return Optional.of(TicketStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value");
        }
    }
}
