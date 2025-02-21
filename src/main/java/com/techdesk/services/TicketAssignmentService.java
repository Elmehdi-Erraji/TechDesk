package com.techdesk.services;

import com.techdesk.entities.AppUser;
import com.techdesk.entities.Ticket;

/**
 * Service interface for assigning tickets to support agents.
 * <p>
 * This interface defines the contract for selecting an appropriate support agent for a given ticket.
 * Implementations should determine the best available agent based on criteria such as current workload.
 * </p>
 */
public interface TicketAssignmentService {

    /**
     * Assigns the given ticket to an available support agent.
     *
     * @param ticket the ticket to assign
     * @return the {@link AppUser} representing the support agent assigned to the ticket
     * @throws com.techdesk.web.errors.NoSupportAgentAvailableException if no support agents are available for assignment
     */
    AppUser assignTicket(Ticket ticket);
}