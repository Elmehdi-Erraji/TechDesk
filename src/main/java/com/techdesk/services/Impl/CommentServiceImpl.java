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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class CommentServiceImpl implements CommentService {

    private final TicketRepository ticketRepository;
    private final AppUserRepository appUserRepository;
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final AuditLogService auditLogService;

    public CommentServiceImpl(TicketRepository ticketRepository,
                              AppUserRepository appUserRepository,
                              CommentRepository commentRepository,
                              CommentMapper commentMapper, AuditLogService auditLogService) {
        this.ticketRepository = ticketRepository;
        this.appUserRepository = appUserRepository;
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.auditLogService = auditLogService;
    }

    @Override
    public CommentResponseDTO addCommentToTicket(UUID ticketId, CommentRequestDTO commentRequestDTO, UUID supportUserId) {
        AppUser supportUser = appUserRepository.findById(supportUserId)
                .orElseThrow(() -> new IllegalArgumentException("Support user not found"));
        if (!"IT_SUPPORT".equals(supportUser.getRole().name())) {
            throw new IllegalArgumentException("Only IT support agents can add comments");
        }
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        Comment comment = commentMapper.commentRequestDTOToComment(commentRequestDTO);
        comment.setTicket(ticket);
        comment.setUser(supportUser);
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepository.save(comment);

        auditLogService.logCommentAdded(ticket, supportUser, comment.getText());

        return commentMapper.commentToCommentResponseDTO(savedComment);
    }


    @Override
    public Page<CommentResponseDTO> getCommentsForTicket(UUID ticketId, Pageable pageable) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
        return commentRepository.findByTicket(ticket, pageable)
                .map(commentMapper::commentToCommentResponseDTO);
    }
}