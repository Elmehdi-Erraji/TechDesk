package com.techdesk.services;

import com.techdesk.entities.Ticket;
import com.techdesk.entities.AppUser;
import com.techdesk.entities.TicketAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AuditLogService {
    void logStatusChange(Ticket ticket, AppUser changedBy, String oldStatus, String newStatus);
    void logCommentAdded(Ticket ticket, AppUser changedBy, String commentText);

    Page<TicketAuditLog> getLogsForTicket(Ticket ticket, Pageable pageable);

    Page<TicketAuditLog> getAllLogs(Pageable pageable);
}
