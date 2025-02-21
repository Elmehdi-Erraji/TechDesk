package com.techdesk.repositories;

import com.techdesk.entities.TicketAuditLog;
import com.techdesk.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<TicketAuditLog, UUID> {
    List<TicketAuditLog> findByTicket(Ticket ticket);
}
