package com.techdesk.services;

import com.techdesk.entities.Ticket;
import com.techdesk.entities.AppUser;

public interface AuditLogService {
    void logStatusChange(Ticket ticket, AppUser changedBy, String oldStatus, String newStatus);
    void logCommentAdded(Ticket ticket, AppUser changedBy, String commentText);
}
