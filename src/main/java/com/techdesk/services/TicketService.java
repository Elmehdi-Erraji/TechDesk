package com.techdesk.services;

import com.techdesk.dto.CreateTicketDTO;
import com.techdesk.dto.TicketResponseDTO;
import com.techdesk.dto.UpdateTicketEmployeeDTO;
import com.techdesk.dto.UpdateTicketStatusDTO;
import com.techdesk.entities.Ticket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

/**
 * Service interface for managing tickets.
 *
 * Provides methods for both employees and IT support agents. Employees can create tickets and view their own tickets,
 * while IT support agents can view and update ticket statuses, as well as search for tickets.
 */
public interface TicketService {

    // Employee operations

    /**
     * Creates a new ticket based on the provided ticket details and associates it with the given employee.
     *
     * @param createTicketDTO the data transfer object containing ticket details
     * @param employeeId the unique identifier of the employee creating the ticket
     * @return a TicketResponseDTO representing the created ticket
     * @throws IllegalArgumentException if the employee is not found or validation fails
     */
    TicketResponseDTO createTicket(CreateTicketDTO createTicketDTO, UUID employeeId);

    /**
     * Retrieves a paginated list of tickets created by the specified employee.
     *
     * @param employeeId the unique identifier of the employee
     * @param pageable pagination and sorting information
     * @return a page of TicketResponseDTO objects representing the employee's tickets
     */
    Page<TicketResponseDTO> getTicketsForEmployee(UUID employeeId, Pageable pageable);

    /**
     * Retrieves a ticket by its unique identifier, ensuring that the ticket was created by the specified employee.
     *
     * @param ticketId the unique identifier of the ticket
     * @param employeeId the unique identifier of the employee
     * @return a TicketResponseDTO representing the ticket
     * @throws IllegalArgumentException if the ticket is not found or if the employee is not the creator of the ticket
     */
    TicketResponseDTO getTicketByIdForEmployee(UUID ticketId, UUID employeeId);

    // IT Support operations

    /**
     * Retrieves a paginated list of all tickets.
     *
     * @param pageable pagination and sorting information
     * @return a page of TicketResponseDTO objects representing all tickets in the system
     */
    Page<TicketResponseDTO> getAllTickets(Pageable pageable);

    /**
     * Updates the status of an existing ticket.
     *
     * @param ticketId the unique identifier of the ticket to update
     * @param supportUserId the unique identifier of the support user performing the update
     * @param updateDTO the data transfer object containing the new status
     * @return a TicketResponseDTO representing the updated ticket
     * @throws com.techdesk.web.errors.SupportUserNotFoundException if the support user is not found
     * @throws com.techdesk.web.errors.UnauthorizedAccessException if the support user is not authorized to update the ticket status
     */
    TicketResponseDTO updateTicketStatus(UUID ticketId, UUID supportUserId, UpdateTicketStatusDTO updateDTO);

    /**
     * Searches for tickets based on optional ticket ID and status filters.
     *
     * This method uses dynamic filtering via JPA Specifications. If no tickets match the criteria, a
     * com.techdesk.web.errors.TicketNotFoundException is thrown.
     *
     * @param ticketId the ticket ID as a string; may be null or empty if not filtering by ticket ID
     * @param status the status filter as a string; may be null or empty if not filtering by status
     * @param pageable pagination and sorting information
     * @return a page of TicketResponseDTO objects matching the search criteria
     * @throws com.techdesk.web.errors.TicketNotFoundException if no tickets are found with the provided criteria
     */
    Page<TicketResponseDTO> searchTickets(String ticketId, String status, Pageable pageable);

    /**
     * Retrieves a ticket entity by its unique identifier.
     *
     * @param ticketId the unique identifier of the ticket
     * @return an Optional containing the Ticket entity if found, otherwise an empty Optional
     * @throws com.techdesk.web.errors.TicketNotFoundException if the ticket is not found
     */
    Optional<Ticket> findById(UUID ticketId);

    /**
     * Updates a ticket by its creator (employee).
     *
     * This method allows an employee to update their own ticket. The update is permitted only if:
     * - The employee is the original creator of the ticket.
     * - The current status of the ticket is "NEW" (i.e. not yet processed).
     *
     * The update is performed using the details provided in the UpdateTicketEmployeeDTO,
     * which includes the new title, description, priority, and category.
     *
     * @param ticketId the unique identifier of the ticket to update
     * @param employeeId the unique identifier of the employee attempting the update
     * @param updateDTO the data transfer object containing the updated ticket details
     * @return a TicketResponseDTO representing the updated ticket
     * @throws com.techdesk.web.errors.TicketNotFoundException if the ticket is not found
     * @throws com.techdesk.web.errors.UnauthorizedAccessException if the employee is not the creator of the ticket
     * @throws IllegalArgumentException if the ticket status is not "NEW"
     */
    TicketResponseDTO updateTicketByEmployee(UUID ticketId, UUID employeeId, UpdateTicketEmployeeDTO updateDTO);

    /**
     * Deletes a ticket by its creator (employee) or by an IT support user.
     *
     * For an employee, deletion is permitted only if the employee is the creator of the ticket
     * and the ticket status is "NEW" (i.e. it has not yet been processed or is in progress).
     * IT support users may also be allowed to delete tickets based on business rules.
     *
     * @param ticketId the unique identifier of the ticket to delete
     * @param userId the unique identifier of the user (employee or IT support) requesting deletion
     * @throws com.techdesk.web.errors.TicketNotFoundException if the ticket is not found
     * @throws com.techdesk.web.errors.UnauthorizedAccessException if the user is not allowed to delete the ticket
     * @throws IllegalArgumentException if the ticket status is not "NEW" (in the case of employee deletion)
     */
    void deleteTicket(UUID ticketId, UUID userId);

}
