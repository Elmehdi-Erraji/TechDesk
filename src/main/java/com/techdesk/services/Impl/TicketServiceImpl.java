package com.techdesk.services.Impl;

import com.techdesk.dto.CreateTicketDTO;
import com.techdesk.dto.TicketResponseDTO;
import com.techdesk.dto.UpdateTicketStatusDTO;
import com.techdesk.dto.mappers.TicketMapper;
import com.techdesk.entities.AppUser;
import com.techdesk.entities.Ticket;
import com.techdesk.entities.enums.TicketStatus;
import com.techdesk.repositories.AppUserRepository;
import com.techdesk.repositories.TicketRepository;
import com.techdesk.services.TicketAssignmentService;
import com.techdesk.services.TicketService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final AppUserRepository appUserRepository;
    private final TicketMapper ticketMapper;
    private final TicketAssignmentService ticketAssignmentService;


    public TicketServiceImpl(TicketRepository ticketRepository, AppUserRepository appUserRepository, TicketMapper ticketMapper, TicketAssignmentService ticketAssignmentService) {
        this.ticketRepository = ticketRepository;
        this.appUserRepository = appUserRepository;
        this.ticketMapper = ticketMapper;
        this.ticketAssignmentService = ticketAssignmentService;
    }


    @Override
    public TicketResponseDTO createTicket(CreateTicketDTO createTicketDTO, UUID employeeId) {
        AppUser employee = appUserRepository.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        Ticket ticket = ticketMapper.createTicketDTOToTicket(createTicketDTO);
        ticket.setCreatedBy(employee);
        ticket.setStatus(TicketStatus.NEW);
        ticket.setCreatedAt(LocalDateTime.now());
        // Assign the ticket using the TicketAssignmentService
        AppUser assignedAgent = ticketAssignmentService.assignTicket(ticket);
        ticket.setAssignedTo(assignedAgent);
        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.ticketToTicketResponseDTO(savedTicket);
    }


    @Override
    public Page<TicketResponseDTO> getTicketsForEmployee(UUID employeeId, Pageable pageable) {
        Page<Ticket> tickets = ticketRepository.findByCreatedById(employeeId, pageable);
        return tickets.map(ticketMapper::ticketToTicketResponseDTO);
    }

    @Override
    public TicketResponseDTO getTicketByIdForEmployee(UUID ticketId, UUID employeeId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        if (!ticket.getCreatedBy().getId().equals(employeeId)) {
            throw new IllegalArgumentException("Access denied");
        }
        return ticketMapper.ticketToTicketResponseDTO(ticket);
    }


    // IT Support

    @Override
    public List<TicketResponseDTO> getAllTickets() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
                .map(ticketMapper::ticketToTicketResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TicketResponseDTO updateTicketStatus(UUID ticketId, UpdateTicketStatusDTO updateDTO, UUID supportUserId) {
        AppUser supportUser = appUserRepository.findById(supportUserId)
                .orElseThrow(() -> new IllegalArgumentException("Support user not found"));
        if (!"IT_SUPPORT".equals(supportUser.getRole().name())) {
            throw new IllegalArgumentException("Only IT support can update ticket status");
        }
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        ticket.setStatus(updateDTO.getStatus());
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updatedTicket = ticketRepository.save(ticket);
        // Optionally, add audit logging here.
        return ticketMapper.ticketToTicketResponseDTO(updatedTicket);
    }



}
