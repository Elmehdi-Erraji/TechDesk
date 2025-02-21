package com.techdesk.services.Impl;

import com.techdesk.dto.CommentRequestDTO;
import com.techdesk.dto.CommentResponseDTO;
import com.techdesk.dto.mappers.CommentMapper;
import com.techdesk.entities.AppUser;
import com.techdesk.entities.Comment;
import com.techdesk.entities.Ticket;
import com.techdesk.repositories.AppUserRepository;
import com.techdesk.repositories.CommentRepository;
import com.techdesk.repositories.TicketRepository;
import com.techdesk.services.AuditLogService;
import com.techdesk.services.CommentService;
import com.techdesk.services.TicketService;
import com.techdesk.services.UserService;
import com.techdesk.web.errors.SupportUserNotFoundException;
import com.techdesk.web.errors.TicketNotFoundException;
import com.techdesk.web.errors.UnauthorizedAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;



/**
 * Implementation of the {@link CommentService} interface.
 * <p>
 * This class provides functionality for adding comments to tickets and retrieving comments from a ticket.
 * It ensures that only authorized IT support users can add comments and logs audit events for comment additions.
 * </p>
 */
@Service
public class CommentServiceImpl implements CommentService {

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final TicketService ticketService;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AuditLogService auditLogService;

    /**
     * Constructs a {@code CommentServiceImpl} with the required dependencies.
     *
     * @param ticketRepository the repository for managing tickets (not directly used here)
     * @param appUserRepository the repository for managing AppUser entities (not directly used here)
     * @param ticketService the service for handling ticket operations
     * @param userService the service for handling user operations
     * @param commentRepository the repository for managing comments
     * @param commentMapper the mapper for converting between Comment entities and DTOs
     * @param auditLogService the service for logging audit events
     */
    public CommentServiceImpl(TicketRepository ticketRepository,
                              AppUserRepository appUserRepository,
                              TicketService ticketService,
                              UserService userService,
                              CommentRepository commentRepository,
                              CommentMapper commentMapper,
                              AuditLogService auditLogService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.auditLogService = auditLogService;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public CommentResponseDTO addCommentToTicket(UUID ticketId, CommentRequestDTO commentRequestDTO, UUID supportUserId) {
        AppUser supportUser = userService.findById(supportUserId)
                .orElseThrow(() -> new SupportUserNotFoundException("Support user not found"));
        if (!"IT_SUPPORT".equals(supportUser.getRole().name())) {
            throw new UnauthorizedAccessException("Only IT support agents can add comments");
        }
        Ticket ticket = ticketService.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        Comment comment = commentMapper.commentRequestDTOToComment(commentRequestDTO);
        comment.setTicket(ticket);
        comment.setUser(supportUser);
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        auditLogService.logCommentAdded(ticket, supportUser, comment.getText());
        logger.info("Support user '{}' added a comment to ticket '{}'", supportUser.getUsername(), ticket.getId());

        return commentMapper.commentToCommentResponseDTO(savedComment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<CommentResponseDTO> getCommentsForTicket(UUID ticketId, Pageable pageable) {
        Ticket ticket = ticketService.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException("Ticket not found"));
        return commentRepository.findByTicket(ticket, pageable)
                .map(commentMapper::commentToCommentResponseDTO);
    }
}