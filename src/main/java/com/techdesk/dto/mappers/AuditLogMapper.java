package com.techdesk.dto.mappers;


import com.techdesk.dto.AuditLogResponseDTO;
import com.techdesk.entities.TicketAuditLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuditLogMapper {

    @Mapping(source = "ticket.id", target = "ticketId")
    @Mapping(source = "changedBy.username", target = "changedByUsername")
    AuditLogResponseDTO toDto(TicketAuditLog auditLog);
}