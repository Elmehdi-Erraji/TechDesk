package com.techdesk.repositories;

import com.techdesk.entities.Comment;
import com.techdesk.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    Page<Comment> findByTicket(Ticket ticket, Pageable pageable);
}
