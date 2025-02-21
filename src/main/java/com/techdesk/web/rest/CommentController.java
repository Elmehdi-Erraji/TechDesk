package com.techdesk.web.rest;

import com.techdesk.dto.CommentRequestDTO;
import com.techdesk.dto.CommentResponseDTO;
import com.techdesk.services.CommentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/tickets")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{ticketId}/comments")
    public ResponseEntity<CommentResponseDTO> addCommentToTicket(
            @PathVariable UUID ticketId,
            @Valid @RequestBody CommentRequestDTO commentRequestDTO,
            @RequestParam UUID supportUserId) {
        CommentResponseDTO response = commentService.addCommentToTicket(ticketId, commentRequestDTO, supportUserId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{ticketId}/comments")
    public ResponseEntity<Page<CommentResponseDTO>> getCommentsForTicket(
            @PathVariable UUID ticketId,
            Pageable pageable) {
        Page<CommentResponseDTO> comments = commentService.getCommentsForTicket(ticketId, pageable);
        return ResponseEntity.ok(comments);
    }
}
