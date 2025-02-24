package com.techdesk.services.Impl;

import com.techdesk.dto.*;
import com.techdesk.dto.mappers.TicketMapper;
import com.techdesk.entities.AppUser;
import com.techdesk.entities.Comment;
import com.techdesk.entities.Ticket;
import com.techdesk.entities.enums.TicketCategory;
import com.techdesk.entities.enums.TicketPriority;
import com.techdesk.entities.enums.TicketStatus;
import com.techdesk.repositories.TicketRepository;
import com.techdesk.services.*;
import com.techdesk.utils.TicketSearchUtil;
import com.techdesk.web.errors.SupportUserNotFoundException;
import com.techdesk.web.errors.TicketNotFoundException;
import com.techdesk.web.errors.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the {@link TicketService} interface.
 * <p>
 * This class handles ticket creation, retrieval, status updates, and searches.
 * It enforces business rules such as ensuring that only the creator can view their tickets and only IT support users
 * can update ticket statuses.
 * </p>
 */
@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final TicketAssignmentService ticketAssignmentService;
    private final AuditLogService auditLogService;
    private final UserService userService;
    private final CommentService commentService;
    private static final Logger logger = LoggerFactory.getLogger(TicketServiceImpl.class);

    /**
     * Constructs a {@code TicketServiceImpl} with the required dependencies.
     *
     * @param ticketRepository       the repository for managing Ticket entities
     * @param ticketMapper           the mapper for converting between Ticket entities and DTOs
     * @param ticketAssignmentService the service for assigning tickets to support agents
     * @param auditLogService        the service for logging audit events related to tickets
     * @param userService            the service for managing user operations
     */
    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper,
                             TicketAssignmentService ticketAssignmentService, AuditLogService auditLogService,
                             UserService userService, CommentService commentService) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.ticketAssignmentService = ticketAssignmentService;
        this.auditLogService = auditLogService;
        this.userService = userService;
        this.commentService = commentService;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Ticket> findById(UUID ticketId) {
        return Optional.of(getTicketEntityById(ticketId));
    }

    /**
     * Helper method to retrieve a Ticket entity by its unique identifier.
     *
     * @param ticketId the unique identifier of the ticket
     * @return the {@link Ticket} entity if found
     * @throws TicketNotFoundException if the ticket is not found
     */
    private Ticket getTicketEntityById(UUID ticketId) {
        return ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found with id " + ticketId));
    }

    /**
     * {@inheritDoc}
     */
    public Page<TicketResponseDTO> getTicketsForEmployee(UUID employeeId, Pageable pageable) {
        Page<Ticket> tickets = ticketRepository.findByCreatedById(employeeId, pageable);
        return tickets.map(ticket -> {
            TicketResponseDTO dto = ticketMapper.ticketToTicketResponseDTO(ticket);
            Page<CommentResponseDTO> commentPage = commentService.getCommentsForTicket(
                    ticket.getId(), PageRequest.of(0, Integer.MAX_VALUE));
            dto.setComments(commentPage.getContent());
            return dto;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TicketResponseDTO getTicketByIdForEmployee(UUID ticketId, UUID employeeId) {
        Ticket ticket = getTicketEntityById(ticketId);
        if (!ticket.getCreatedBy().getId().equals(employeeId)) {
            throw new IllegalArgumentException("Access denied");
        }
        return ticketMapper.ticketToTicketResponseDTO(ticket);
    }

    // IT Support operations

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TicketResponseDTO> getAllTickets(Pageable pageable) {
        // Fetch tickets with pagination
        Page<Ticket> tickets = ticketRepository.findAll(pageable);

        // Map tickets to TicketResponseDTO and include comments
        return tickets.map(ticket -> {
            TicketResponseDTO dto = ticketMapper.ticketToTicketResponseDTO(ticket);

            // Fetch comments for the ticket
            Page<CommentResponseDTO> commentPage = commentService.getCommentsForTicket(
                    ticket.getId(), PageRequest.of(0, Integer.MAX_VALUE)); // Fetch all comments
            dto.setComments(commentPage.getContent());

            return dto;
        });
    }

    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    public TicketResponseDTO updateTicketByEmployee(UUID ticketId, UUID employeeId, UpdateTicketEmployeeDTO updateDTO) {
        Ticket ticket = getTicketEntityById(ticketId);
        if (!ticket.getCreatedBy().getId().equals(employeeId)) {
            throw new UnauthorizedAccessException("Employee is not the creator of this ticket");
        }
        if (!ticket.getStatus().equals(TicketStatus.NEW)) {
            throw new IllegalArgumentException("Ticket can only be updated if its status is NEW");
        }
        ticket.setTitle(updateDTO.getTitle());
        ticket.setDescription(updateDTO.getDescription());
        ticket.setPriority(TicketPriority.valueOf(updateDTO.getPriority()));
        ticket.setCategory(TicketCategory.valueOf(updateDTO.getCategory()));
        ticket.setUpdatedAt(LocalDateTime.now());
        Ticket updatedTicket = ticketRepository.save(ticket);
        logger.info("Ticket {} updated by employee {}",
                ticket.getId(), ticket.getCreatedBy().getUsername());
        return ticketMapper.ticketToTicketResponseDTO(updatedTicket);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteTicket(UUID ticketId, UUID userId) {
        Ticket ticket = getTicketEntityById(ticketId);
        AppUser user = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id " + userId));

        if (!user.getRole().name().equals("IT_SUPPORT")) {
            if (!ticket.getCreatedBy().getId().equals(userId)) {
                throw new UnauthorizedAccessException("Employee is not the creator of this ticket");
            }
            if (ticket.getStatus().equals(TicketStatus.IN_PROGRESS)) {
                throw new IllegalArgumentException("Cannot delete a ticket that is in progress");
            }
        }

        if (ticket.getComments() != null && !ticket.getComments().isEmpty()) {
            for (Comment comment : ticket.getComments()) {
                commentService.deleteComment(comment.getId());
            }
        }

        auditLogService.deleteLogsForTicket(ticket);

        ticketRepository.delete(ticket);
        logger.info("Ticket {} deleted by user {}", ticket.getId(), user.getUsername());
    }


    /**
     * {@inheritDoc}
     */
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
