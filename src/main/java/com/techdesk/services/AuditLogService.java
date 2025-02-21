package com.techdesk.services;

import com.techdesk.entities.Ticket;
import com.techdesk.entities.AppUser;
import com.techdesk.entities.TicketAuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for audit logging related to ticket changes.
 * <p>
 * This interface provides methods to log status changes and comment additions on tickets,
 * as well as methods to retrieve audit logs.
 * </p>
 */
public interface AuditLogService {

    /**
     * Logs a status change event for the specified ticket.
     *
     * @param ticket    the ticket whose status has changed
     * @param changedBy the user who made the change
     * @param oldStatus the previous status of the ticket
     * @param newStatus the new status of the ticket
     */
    void logStatusChange(Ticket ticket, AppUser changedBy, String oldStatus, String newStatus);

    /**
     * Logs an event for a comment added to the specified ticket.
     *
     * @param ticket      the ticket to which the comment was added
     * @param changedBy   the user who added the comment
     * @param commentText the text of the comment that was added
     */
    void logCommentAdded(Ticket ticket, AppUser changedBy, String commentText);

    /**
     * Retrieves a paginated list of audit logs for the specified ticket.
     *
     * @param ticket   the ticket for which to retrieve audit logs
     * @param pageable pagination and sorting information
     * @return a page of {@link TicketAuditLog} objects for the given ticket
     */
    Page<TicketAuditLog> getLogsForTicket(Ticket ticket, Pageable pageable);

    /**
     * Retrieves a paginated list of all audit logs.
     *
     * @param pageable pagination and sorting information
     * @return a page of all {@link TicketAuditLog} objects
     */
    Page<TicketAuditLog> getAllLogs(Pageable pageable);
}