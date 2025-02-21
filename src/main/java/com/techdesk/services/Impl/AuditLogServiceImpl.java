package com.techdesk.services.Impl;

import com.techdesk.entities.Ticket;
import com.techdesk.entities.TicketAuditLog;
import com.techdesk.entities.AppUser;
import com.techdesk.entities.enums.AuditLogType;
import com.techdesk.repositories.AuditLogRepository;
import com.techdesk.services.AuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditLogServiceImpl implements AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogServiceImpl.class);

    private final AuditLogRepository auditLogRepository;

    public AuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public void logStatusChange(Ticket ticket, AppUser changedBy, String oldStatus, String newStatus) {
        TicketAuditLog log = TicketAuditLog.builder()
                .ticket(ticket)
                .changedBy(changedBy)
                .logType(AuditLogType.STATUS_CHANGE)
                .description("Status changed from " + oldStatus + " to " + newStatus)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
        logger.info("Audit log created: Ticket {} status changed from {} to {} by user {}",
                ticket.getId(), oldStatus, newStatus, changedBy.getUsername());
    }

    @Override
    public void logCommentAdded(Ticket ticket, AppUser changedBy, String commentText) {
        TicketAuditLog log = TicketAuditLog.builder()
                .ticket(ticket)
                .changedBy(changedBy)
                .logType(AuditLogType.COMMENT_ADDED)
                .description("Comment added: " + commentText)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepository.save(log);
        logger.info("Audit log created: Comment added to Ticket {} by user {}",
                ticket.getId(), changedBy.getUsername());
    }

    @Override
    public Page<TicketAuditLog> getLogsForTicket(Ticket ticket, Pageable pageable) {
        return auditLogRepository.findByTicket(ticket, pageable);
    }

    @Override
    public Page<TicketAuditLog> getAllLogs(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }
}
