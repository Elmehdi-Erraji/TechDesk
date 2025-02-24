package com.techdesk.services;

import com.techdesk.dto.CommentRequestDTO;
import com.techdesk.dto.CommentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;


/**
 * Service interface for handling ticket comments.
 * <p>
 * This interface defines methods to add comments to a ticket and to retrieve comments for a given ticket.
 * </p>
 */
public interface CommentService {

    /**
     * Adds a comment to the specified ticket.
     *
     * @param ticketId          the unique identifier of the ticket to which the comment is added
     * @param commentRequestDTO the data transfer object containing the comment details
     * @param supportUserId     the unique identifier of the support user adding the comment
     * @return a {@link CommentResponseDTO} representing the newly added comment
     * @throws com.techdesk.web.errors.SupportUserNotFoundException if the support user is not found
     * @throws com.techdesk.web.errors.UnauthorizedAccessException  if the user is not authorized to add comments
     * @throws com.techdesk.web.errors.TicketNotFoundException       if the ticket is not found
     */
    CommentResponseDTO addCommentToTicket(UUID ticketId, CommentRequestDTO commentRequestDTO, UUID supportUserId);


    /**
     * Retrieves a paginated list of comments for the specified ticket.
     *
     * @param ticketId the unique identifier of the ticket
     * @param pageable pagination and sorting information
     * @return a page of {@link CommentResponseDTO} objects associated with the ticket
     * @throws com.techdesk.web.errors.TicketNotFoundException if the ticket is not found
     */
    Page<CommentResponseDTO> getCommentsForTicket(UUID ticketId, Pageable pageable);

    /**
     * Deletes the comment with the specified identifier.
     * <p>
     * This method is used to remove a comment from a ticket. The deletion must occur before the ticket is deleted
     * to satisfy foreign key constraints.
     * </p>
     *
     * @param commentId the unique identifier of the comment to delete
     */
    void deleteComment(UUID commentId);
}
