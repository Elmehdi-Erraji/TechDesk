package com.techdesk.services;

import com.techdesk.dto.CreateTicketDTO;
import com.techdesk.dto.TicketResponseDTO;
import com.techdesk.dto.UpdateTicketStatusDTO;
import com.techdesk.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TicketService {
    // Employee
    TicketResponseDTO createTicket(CreateTicketDTO createTicketDTO, UUID employeeId);
    Page<TicketResponseDTO> getTicketsForEmployee(UUID employeeId, Pageable pageable);
    TicketResponseDTO getTicketByIdForEmployee(UUID ticketId, UUID employeeId);

    // IT Support
    List<TicketResponseDTO> getAllTickets();
    TicketResponseDTO updateTicketStatus(UUID ticketId, UUID supportUserId, UpdateTicketStatusDTO updateDTO);

    Page<TicketResponseDTO> searchTickets(String ticketId, String status, Pageable pageable);

    Optional<Ticket> findById(UUID ticketId);

}
