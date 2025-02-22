package com.techdesk;

import com.techdesk.dto.CommentRequestDTO;
import com.techdesk.dto.CommentResponseDTO;
import com.techdesk.dto.mappers.CommentMapper;
import com.techdesk.entities.AppUser;
import com.techdesk.entities.Comment;
import com.techdesk.entities.Ticket;
import com.techdesk.entities.enums.Role;
import com.techdesk.repositories.CommentRepository;
import com.techdesk.services.AuditLogService;
import com.techdesk.services.Impl.CommentServiceImpl;
import com.techdesk.services.TicketService;
import com.techdesk.services.UserService;
import com.techdesk.web.errors.SupportUserNotFoundException;
import com.techdesk.web.errors.TicketNotFoundException;
import com.techdesk.web.errors.UnauthorizedAccessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    @Mock
    private TicketService ticketService;

    @Mock
    private UserService userService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private AuditLogService auditLogService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UUID ticketId;
    private UUID supportUserId;
    private CommentRequestDTO commentRequestDTO;
    private AppUser supportUser;
    private Ticket ticket;
    private Comment comment;
    private CommentResponseDTO commentResponseDTO;

    @BeforeEach
    void setUp() {
        ticketId = UUID.randomUUID();
        supportUserId = UUID.randomUUID();

        commentRequestDTO = new CommentRequestDTO();
        commentRequestDTO.setText("Test comment");

        supportUser = new AppUser();
        supportUser.setId(supportUserId);
        supportUser.setUsername("supportUser");
        supportUser.setRole(Role.IT_SUPPORT);

        ticket = new Ticket();
        ticket.setId(ticketId);

        comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setText("Test comment");
        comment.setUser(supportUser);
        comment.setTicket(ticket);
        comment.setCreatedAt(LocalDateTime.now());

        commentResponseDTO = new CommentResponseDTO();
        commentResponseDTO.setId(comment.getId());
        commentResponseDTO.setText(comment.getText());
        commentResponseDTO.setCreatedAt(comment.getCreatedAt());
    }

    // 1. Happy Path: Add Comment Successfully
    @Test
    void addCommentToTicket_Success_ReturnsCommentResponseDTO() {
        // Arrange
        when(userService.findById(supportUserId)).thenReturn(Optional.of(supportUser));
        when(ticketService.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(commentMapper.commentRequestDTOToComment(commentRequestDTO)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.commentToCommentResponseDTO(comment)).thenReturn(commentResponseDTO);

        // Act
        CommentResponseDTO result = commentService.addCommentToTicket(ticketId, commentRequestDTO, supportUserId);

        // Assert
        assertNotNull(result, "CommentResponseDTO should not be null");
        assertEquals(commentResponseDTO, result, "Returned CommentResponseDTO should match the expected value");
        verify(userService, times(1)).findById(supportUserId);
        verify(ticketService, times(1)).findById(ticketId);
        verify(commentRepository, times(1)).save(comment);
        verify(auditLogService, times(1)).logCommentAdded(ticket, supportUser, comment.getText());
    }

    // 2. Support User Not Found
    @Test
    void addCommentToTicket_SupportUserNotFound_ThrowsException() {
        // Arrange
        when(userService.findById(supportUserId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SupportUserNotFoundException.class, () ->
                        commentService.addCommentToTicket(ticketId, commentRequestDTO, supportUserId),
                "SupportUserNotFoundException should be thrown");
        verify(userService, times(1)).findById(supportUserId);
        verify(ticketService, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    // 3. Unauthorized User
    @Test
    void addCommentToTicket_UnauthorizedUser_ThrowsException() {
        // Arrange
        supportUser.setRole(Role.EMPLOYEE); // Not an IT support agent
        when(userService.findById(supportUserId)).thenReturn(Optional.of(supportUser));

        // Act & Assert
        assertThrows(UnauthorizedAccessException.class, () ->
                        commentService.addCommentToTicket(ticketId, commentRequestDTO, supportUserId),
                "UnauthorizedAccessException should be thrown");
        verify(userService, times(1)).findById(supportUserId);
        verify(ticketService, never()).findById(any());
        verify(commentRepository, never()).save(any());
    }

    // 4. Ticket Not Found
    @Test
    void addCommentToTicket_TicketNotFound_ThrowsException() {
        // Arrange
        when(userService.findById(supportUserId)).thenReturn(Optional.of(supportUser));
        when(ticketService.findById(ticketId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TicketNotFoundException.class, () ->
                        commentService.addCommentToTicket(ticketId, commentRequestDTO, supportUserId),
                "TicketNotFoundException should be thrown");
        verify(userService, times(1)).findById(supportUserId);
        verify(ticketService, times(1)).findById(ticketId);
        verify(commentRepository, never()).save(any());
    }

    // 5. Repository Throws an Exception
    @Test
    void addCommentToTicket_RepositoryThrowsException_PropagatesException() {
        // Arrange
        when(userService.findById(supportUserId)).thenReturn(Optional.of(supportUser));
        when(ticketService.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(commentMapper.commentRequestDTOToComment(commentRequestDTO)).thenReturn(comment);
        when(commentRepository.save(comment)).thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        assertThrows(DataAccessException.class, () ->
                        commentService.addCommentToTicket(ticketId, commentRequestDTO, supportUserId),
                "DataAccessException should be propagated");
        verify(userService, times(1)).findById(supportUserId);
        verify(ticketService, times(1)).findById(ticketId);
        verify(commentRepository, times(1)).save(comment);
    }



    // 1. Happy Path: Retrieve Comments Successfully
    @Test
    void getCommentsForTicket_Success_ReturnsPageOfComments() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Comment> commentPage = new PageImpl<>(Collections.singletonList(comment));
        when(ticketService.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(commentRepository.findByTicket(ticket, pageable)).thenReturn(commentPage);
        when(commentMapper.commentToCommentResponseDTO(comment)).thenReturn(commentResponseDTO);

        // Act
        Page<CommentResponseDTO> result = commentService.getCommentsForTicket(ticketId, pageable);

        // Assert
        assertNotNull(result, "Page of CommentResponseDTO should not be null");
        assertEquals(1, result.getTotalElements(), "Page should contain one comment");
        verify(ticketService, times(1)).findById(ticketId);
        verify(commentRepository, times(1)).findByTicket(ticket, pageable);
    }

    // 2. Ticket Not Found
    @Test
    void getCommentsForTicket_TicketNotFound_ThrowsException() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        when(ticketService.findById(ticketId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TicketNotFoundException.class, () ->
                        commentService.getCommentsForTicket(ticketId, pageable),
                "TicketNotFoundException should be thrown");
        verify(ticketService, times(1)).findById(ticketId);
        verify(commentRepository, never()).findByTicket(any(), any());
    }

    // 3. No Comments Found
    @Test
    void getCommentsForTicket_NoCommentsFound_ReturnsEmptyPage() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Comment> emptyPage = new PageImpl<>(Collections.emptyList());
        when(ticketService.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(commentRepository.findByTicket(ticket, pageable)).thenReturn(emptyPage);

        // Act
        Page<CommentResponseDTO> result = commentService.getCommentsForTicket(ticketId, pageable);

        // Assert
        assertNotNull(result, "Page of CommentResponseDTO should not be null");
        assertTrue(result.isEmpty(), "Page should be empty");
        verify(ticketService, times(1)).findById(ticketId);
        verify(commentRepository, times(1)).findByTicket(ticket, pageable);
    }

    // 4. Repository Throws an Exception
    @Test
    void getCommentsForTicket_RepositoryThrowsException_PropagatesException() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        when(ticketService.findById(ticketId)).thenReturn(Optional.of(ticket));
        when(commentRepository.findByTicket(ticket, pageable)).thenThrow(new DataAccessException("Database error") {});

        // Act & Assert
        assertThrows(DataAccessException.class, () ->
                        commentService.getCommentsForTicket(ticketId, pageable),
                "DataAccessException should be propagated");
        verify(ticketService, times(1)).findById(ticketId);
        verify(commentRepository, times(1)).findByTicket(ticket, pageable);
    }


}