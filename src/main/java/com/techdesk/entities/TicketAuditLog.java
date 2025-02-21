package com.techdesk.entities;


import com.techdesk.entities.enums.AuditLogType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Table(name = "ticket_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketAuditLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by", nullable = false)
    private AppUser changedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "log_type", nullable = false)
    private AuditLogType logType;

    @Column(name = "description", nullable = false, length = 2000)
    private String description;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;
}
