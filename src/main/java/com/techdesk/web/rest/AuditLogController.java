package com.techdesk.web.rest;

import com.techdesk.dto.AuditLogResponseDTO;
import com.techdesk.dto.mappers.AuditLogMapper;
import com.techdesk.entities.Ticket;
import com.techdesk.entities.TicketAuditLog;
import com.techdesk.repositories.AuditLogRepository;
import com.techdesk.repositories.TicketRepository;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;
    private final TicketRepository ticketRepository;
    private final AuditLogMapper auditLogMapper;

    public AuditLogController(AuditLogRepository auditLogRepository, TicketRepository ticketRepository, AuditLogMapper auditLogMapper) {
        this.auditLogRepository = auditLogRepository;
        this.ticketRepository = ticketRepository;
        this.auditLogMapper = auditLogMapper;
    }

    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<Page<AuditLogResponseDTO>> getLogsForTicket(
            @PathVariable @NotNull UUID ticketId,
            Pageable pageable) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        Page<TicketAuditLog> logsPage = auditLogRepository.findByTicket(ticket, pageable);
        Page<AuditLogResponseDTO> dtoPage = logsPage.map(auditLogMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }

    // Endpoint to retrieve all audit logs (admin view) with pagination.
    @GetMapping
    public ResponseEntity<Page<AuditLogResponseDTO>> getAllLogs(Pageable pageable) {
        Page<TicketAuditLog> logsPage = auditLogRepository.findAll(pageable);
        Page<AuditLogResponseDTO> dtoPage = logsPage.map(auditLogMapper::toDto);
        return ResponseEntity.ok(dtoPage);
    }
}
