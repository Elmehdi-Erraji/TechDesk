package com.techdesk.web.rest;

import com.techdesk.dto.CreateTicketDTO;
import com.techdesk.dto.TicketResponseDTO;
import com.techdesk.dto.UpdateTicketStatusDTO;
import com.techdesk.services.TicketService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Employee endpoints

    @PostMapping("/create")
    public ResponseEntity<TicketResponseDTO> createTicket(
            @Valid @RequestBody CreateTicketDTO createTicketDTO,
            @RequestParam String employeeId) {
        UUID employeeUUID;
        try {
            employeeUUID = UUID.fromString(employeeId);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid employeeId format. Expected a valid UUID string.");
        }
        TicketResponseDTO response = ticketService.createTicket(createTicketDTO, employeeUUID);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee")
    public ResponseEntity<Page<TicketResponseDTO>> getTicketsForEmployee(
            @RequestParam UUID employeeId, Pageable pageable) {
        Page<TicketResponseDTO> tickets = ticketService.getTicketsForEmployee(employeeId, pageable);
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/employee/{ticketId}")
    public ResponseEntity<TicketResponseDTO> getTicketByIdForEmployee(@PathVariable UUID ticketId,
                                                                      @RequestParam UUID employeeId) {
        TicketResponseDTO response = ticketService.getTicketByIdForEmployee(ticketId, employeeId);
        return ResponseEntity.ok(response);
    }

    // IT Support endpoints

    @GetMapping("/all")
    public ResponseEntity<List<TicketResponseDTO>> getAllTickets(@RequestParam UUID supportUserId) {
        List<TicketResponseDTO> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @PutMapping("/{ticketId}/status")
    public ResponseEntity<TicketResponseDTO> updateTicketStatus(@PathVariable UUID ticketId,
                                                                @Valid @RequestBody UpdateTicketStatusDTO updateDTO,
                                                                @RequestParam UUID supportUserId) {
        TicketResponseDTO response = ticketService.updateTicketStatus(ticketId, updateDTO, supportUserId);
        return ResponseEntity.ok(response);
    }


}
