package com.techdesk.services;

import com.techdesk.dto.CreateTicketDTO;
import com.techdesk.dto.TicketResponseDTO;
import com.techdesk.dto.UpdateTicketStatusDTO;
import com.techdesk.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing tickets.
 * <p>
 * Provides methods for both employees and IT support agents. Employees can create tickets and view their own tickets,
 * while IT support agents can view and update ticket statuses, as well as search for tickets.
 * </p>
 */
public interface TicketService {

    // Employee operations

    /**
     * Creates a new ticket based on the provided ticket details and associates it with the given employee.
     *
     * @param createTicketDTO the data transfer object containing ticket details
     * @param employeeId      the unique identifier of the employee creating the ticket
     * @return a {@link TicketResponseDTO} representing the created ticket
     * @throws IllegalArgumentException if the employee is not found or validation fails
     */
    TicketResponseDTO createTicket(CreateTicketDTO createTicketDTO, UUID employeeId);

    /**
     * Retrieves a paginated list of tickets created by the specified employee.
     *
     * @param employeeId the unique identifier of the employee
     * @param pageable   pagination and sorting information
     * @return a page of {@link TicketResponseDTO} objects representing the employee's tickets
     */
    Page<TicketResponseDTO> getTicketsForEmployee(UUID employeeId, Pageable pageable);

    /**
     * Retrieves a ticket by its unique identifier, ensuring that the ticket was created by the specified employee.
     *
     * @param ticketId   the unique identifier of the ticket
     * @param employeeId the unique identifier of the employee
     * @return a {@link TicketResponseDTO} representing the ticket
     * @throws IllegalArgumentException if the ticket is not found or if the employee is not the creator of the ticket
     */
    TicketResponseDTO getTicketByIdForEmployee(UUID ticketId, UUID employeeId);

    // IT Support operations

    /**
     * Retrieves a list of all tickets.
     *
     * @return a list of {@link TicketResponseDTO} objects representing all tickets in the system
     */
    List<TicketResponseDTO> getAllTickets();

    /**
     * Updates the status of an existing ticket.
     *
     * @param ticketId    the unique identifier of the ticket to update
     * @param supportUserId the unique identifier of the support user performing the update
     * @param updateDTO   the data transfer object containing the new status
     * @return a {@link TicketResponseDTO} representing the updated ticket
     * @throws com.techdesk.web.errors.SupportUserNotFoundException if the support user is not found
     * @throws com.techdesk.web.errors.UnauthorizedAccessException  if the support user is not authorized to update the ticket status
     */
    TicketResponseDTO updateTicketStatus(UUID ticketId, UUID supportUserId, UpdateTicketStatusDTO updateDTO);

    /**
     * Searches for tickets based on optional ticket ID and status filters.
     *
     * <p>
     * This method uses dynamic filtering via JPA Specifications. If no tickets match the criteria, a
     * {@link com.techdesk.web.errors.TicketNotFoundException} is thrown.
     * </p>
     *
     * @param ticketId the ticket ID as a string; may be null or empty if not filtering by ticket ID
     * @param status   the status filter as a string; may be null or empty if not filtering by status
     * @param pageable pagination and sorting information
     * @return a page of {@link TicketResponseDTO} objects matching the search criteria
     * @throws com.techdesk.web.errors.TicketNotFoundException if no tickets are found with the provided criteria
     */
    Page<TicketResponseDTO> searchTickets(String ticketId, String status, Pageable pageable);

    /**
     * Retrieves a ticket entity by its unique identifier.
     *
     * @param ticketId the unique identifier of the ticket
     * @return an {@link Optional} containing the {@link Ticket} entity if found, otherwise an empty {@link Optional}
     * @throws com.techdesk.web.errors.TicketNotFoundException if the ticket is not found
     */
    Optional<Ticket> findById(UUID ticketId);
}
