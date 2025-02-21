package com.techdesk.services.Impl;

import com.techdesk.dto.CreateTicketDTO;
import com.techdesk.dto.TicketResponseDTO;
import com.techdesk.dto.UpdateTicketStatusDTO;
import com.techdesk.dto.mappers.TicketMapper;
import com.techdesk.entities.AppUser;
import com.techdesk.entities.Ticket;
import com.techdesk.entities.enums.TicketStatus;
import com.techdesk.repositories.TicketRepository;
import com.techdesk.services.AuditLogService;
import com.techdesk.services.TicketAssignmentService;
import com.techdesk.services.TicketService;
import com.techdesk.services.UserService;
import com.techdesk.utils.TicketSearchUtil;
import com.techdesk.web.errors.SupportUserNotFoundException;
import com.techdesk.web.errors.TicketNotFoundException;
import com.techdesk.web.errors.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final TicketAssignmentService ticketAssignmentService;
    private final AuditLogService auditLogService;
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper,
                             TicketAssignmentService ticketAssignmentService, AuditLogService auditLogService,
                             UserService userService) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.ticketAssignmentService = ticketAssignmentService;
        this.auditLogService = auditLogService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public TicketResponseDTO createTicket(CreateTicketDTO createTicketDTO, UUID employeeId) {
        AppUser employee = userService.findById(employeeId)
                .orElseThrow(() -> new IllegalArgumentException("Employee not found"));
        Ticket ticket = ticketMapper.createTicketDTOToTicket(createTicketDTO);
        ticket.setCreatedBy(employee);
        ticket.setStatus(TicketStatus.NEW);
        ticket.setCreatedAt(LocalDateTime.now());
        AppUser assignedAgent = ticketAssignmentService.assignTicket(ticket);
        ticket.setAssignedTo(assignedAgent);
        Ticket savedTicket = ticketRepository.save(ticket);
        logger.info("Ticket {} created by employee {} and assigned to support agent {}",
                savedTicket.getId(), employee.getUsername(), assignedAgent.getUsername());
        return ticketMapper.ticketToTicketResponseDTO(savedTicket);
    }

    @Override
    public Optional<Ticket> findById(UUID ticketId) {
        return Optional.of(getTicketEntityById(ticketId));
    }

    private Ticket getTicketEntityById(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id " + ticketId));
    }

    @Override
    public Page<TicketResponseDTO> getTicketsForEmployee(UUID employeeId, Pageable pageable) {
        Page<Ticket> tickets = ticketRepository.findByCreatedById(employeeId, pageable);
        return tickets.map(ticketMapper::ticketToTicketResponseDTO);
    }

    @Override
    public TicketResponseDTO getTicketByIdForEmployee(UUID ticketId, UUID employeeId) {
        Ticket ticket = getTicketEntityById(ticketId);
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
    @Transactional
    public TicketResponseDTO updateTicketStatus(UUID ticketId, UUID supportUserId, UpdateTicketStatusDTO updateDTO) {
        AppUser supportUser = userService.findById(supportUserId)
                .orElseThrow(() -> new SupportUserNotFoundException("Support user not found"));
        if (!supportUser.getRole().name().equals("IT_SUPPORT")) {
            throw new UnauthorizedAccessException("Only IT support can update ticket status");
        }
        Ticket ticket = getTicketEntityById(ticketId);
        String oldStatus = ticket.getStatus().name();
        ticket.setStatus(updateDTO.getStatus());
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updatedTicket = ticketRepository.save(ticket);

        auditLogService.logStatusChange(ticket, supportUser, oldStatus, updateDTO.getStatus().name());
        logger.info("Ticket {} status updated from {} to {} by support user {}",
                ticket.getId(), oldStatus, updateDTO.getStatus().name(), supportUser.getUsername());

        return ticketMapper.ticketToTicketResponseDTO(updatedTicket);
    }

    @Override
    public Page<TicketResponseDTO> searchTickets(String ticketId, String status, Pageable pageable) {
        Optional<UUID> ticketUuidOpt = TicketSearchUtil.parseUuid(ticketId);
        Optional<TicketStatus> statusOpt = TicketSearchUtil.parseTicketStatus(status);

        Specification<Ticket> spec = Specification.where(null);

        if (ticketUuidOpt.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("id"), ticketUuidOpt.get()));
        }

        if (statusOpt.isPresent()) {
            spec = spec.and((root, query, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("status"), statusOpt.get()));
        }

        Page<Ticket> tickets = ticketRepository.findAll(spec, pageable);

        if (tickets.isEmpty()) {
            throw new TicketNotFoundException("No tickets found with the provided criteria");
        }

        return tickets.map(ticketMapper::ticketToTicketResponseDTO);
    }
}
