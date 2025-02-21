package com.techdesk.web.rest;

import com.techdesk.entities.Ticket;
import com.techdesk.entities.TicketAuditLog;
import com.techdesk.repositories.AuditLogRepository;
import com.techdesk.repositories.TicketRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final TicketRepository ticketRepository;

    public AuditLogController(AuditLogRepository auditLogRepository, TicketRepository ticketRepository) {
        this.auditLogRepository = auditLogRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<TicketAuditLog>> getLogsForTicket(@PathVariable @NotNull UUID ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        List<TicketAuditLog> logs = auditLogRepository.findByTicket(ticket);
        return ResponseEntity.ok(logs);
    }

    @GetMapping
    public ResponseEntity<List<TicketAuditLog>> getAllLogs() {
        List<TicketAuditLog> logs = auditLogRepository.findAll();
        return ResponseEntity.ok(logs);
    }
}
