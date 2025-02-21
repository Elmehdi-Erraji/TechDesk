package com.techdesk.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CommentResponseDTO {
    private UUID id;
    private String text;
    private LocalDateTime createdAt;
    private String authorUsername;
}
