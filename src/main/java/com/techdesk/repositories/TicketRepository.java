package com.techdesk.repositories;

import com.techdesk.entities.AppUser;
import com.techdesk.entities.Ticket;
import com.techdesk.entities.enums.TicketStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    Page<Ticket> findByCreatedById(UUID createdById, Pageable pageable);

    int countByAssignedToAndStatusNotIn(AppUser assignedTo, List<TicketStatus> statuses);

    Page<Ticket> findByStatus(TicketStatus status, Pageable pageable);

}
