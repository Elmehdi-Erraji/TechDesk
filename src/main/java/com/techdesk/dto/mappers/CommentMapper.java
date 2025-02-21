package com.techdesk.dto.mappers;

import com.techdesk.dto.CommentRequestDTO;
import com.techdesk.dto.CommentResponseDTO;
import com.techdesk.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment commentRequestDTOToComment(CommentRequestDTO dto);

    @Mapping(source = "user.username", target = "authorUsername")
    CommentResponseDTO commentToCommentResponseDTO(Comment comment);
}
