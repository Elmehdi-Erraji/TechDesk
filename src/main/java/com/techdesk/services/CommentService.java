package com.techdesk.services;

import com.techdesk.dto.CommentRequestDTO;
import com.techdesk.dto.CommentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CommentService {
    CommentResponseDTO addCommentToTicket(UUID ticketId, CommentRequestDTO commentRequestDTO, UUID supportUserId);
    Page<CommentResponseDTO> getCommentsForTicket(UUID ticketId, Pageable pageable);
}
