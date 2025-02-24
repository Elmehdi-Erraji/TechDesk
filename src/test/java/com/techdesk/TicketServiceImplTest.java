package com.techdesk;

import com.techdesk.dto.*;
import com.techdesk.dto.mappers.TicketMapper;
import com.techdesk.entities.AppUser;
import com.techdesk.entities.Comment;
import com.techdesk.entities.Ticket;
import com.techdesk.entities.enums.*;
import com.techdesk.repositories.TicketRepository;
import com.techdesk.services.*;
import com.techdesk.services.Impl.TicketServiceImpl;
import com.techdesk.web.errors.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @Mock
    private TicketRepository ticketRepository;
    @Mock
    private TicketMapper ticketMapper;
    @Mock
    private TicketAssignmentService ticketAssignmentService;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private UserService userService;
    @Mock
    private CommentService commentService;

    @InjectMocks
    private TicketServiceImpl ticketService;

    private AppUser employee;
    private AppUser supportUser;
    private Ticket ticket;
    private CreateTicketDTO createTicketDTO;
    private TicketResponseDTO ticketResponseDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employee = new AppUser();
        employee.setId(UUID.randomUUID());
        employee.setUsername("employee1");
        employee.setRole(Role.EMPLOYEE);

        supportUser = new AppUser();
        supportUser.setId(UUID.randomUUID());
        supportUser.setUsername("support1");
        supportUser.setRole(Role.IT_SUPPORT);

        ticket = new Ticket();
        ticket.setId(UUID.randomUUID());
        ticket.setCreatedBy(employee);
        ticket.setStatus(TicketStatus.NEW);
        ticket.setCreatedAt(LocalDateTime.now());

        createTicketDTO = new CreateTicketDTO();
        // Populate createTicketDTO properties as needed

        ticketResponseDTO = new TicketResponseDTO();
        // Populate ticketResponseDTO properties as needed

        // Default mapping behavior
        when(ticketMapper.createTicketDTOToTicket(any(CreateTicketDTO.class))).thenReturn(ticket);
        when(ticketMapper.ticketToTicketResponseDTO(any(Ticket.class))).thenReturn(ticketResponseDTO);
    }

    // createTicket scenarios

    @Test
    void testCreateTicket_Success() {
        when(userService.findById(employee.getId())).thenReturn(Optional.of(employee));
        when(ticketAssignmentService.assignTicket(any(Ticket.class))).thenReturn(supportUser);
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        TicketResponseDTO result = ticketService.createTicket(createTicketDTO, employee.getId());

        assertNotNull(result);
        verify(userService).findById(employee.getId());
        verify(ticketAssignmentService).assignTicket(ticket);
        verify(ticketRepository).save(ticket);
        verify(ticketMapper).ticketToTicketResponseDTO(ticket);
    }

    @Test
    void testCreateTicket_EmployeeNotFound() {
        when(userService.findById(employee.getId())).thenReturn(Optional.empty());

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ticketService.createTicket(createTicketDTO, employee.getId())
        );
        assertEquals("Employee not found", exception.getMessage());
    }

    // findById scenarios

    @Test
    void testFindById_Success() {
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        Optional<Ticket> result = ticketService.findById(ticket.getId());
        assertTrue(result.isPresent());
        assertEquals(ticket, result.get());
    }

    @Test
    void testFindById_NotFound() {
        UUID id = UUID.randomUUID();
        when(ticketRepository.findById(id)).thenReturn(Optional.empty());
        assertThrows(TicketNotFoundException.class, () -> ticketService.findById(id));
    }

    // getTicketsForEmployee scenarios

    @Test
    void testGetTicketsForEmployee_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Ticket> ticketList = Collections.singletonList(ticket);
        Page<Ticket> pageTickets = new PageImpl<>(ticketList, pageable, ticketList.size());
        when(ticketRepository.findByCreatedById(employee.getId(), pageable)).thenReturn(pageTickets);
        // Assume no comments for simplicity.
        when(commentService.getCommentsForTicket(any(UUID.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        Page<TicketResponseDTO> result = ticketService.getTicketsForEmployee(employee.getId(), pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    // getTicketByIdForEmployee scenarios

    @Test
    void testGetTicketByIdForEmployee_Success() {
        ticket.setCreatedBy(employee);
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        TicketResponseDTO result = ticketService.getTicketByIdForEmployee(ticket.getId(), employee.getId());
        assertNotNull(result);
    }

    @Test
    void testGetTicketByIdForEmployee_Unauthorized() {
        AppUser otherEmployee = new AppUser();
        otherEmployee.setId(UUID.randomUUID());
        ticket.setCreatedBy(employee);
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ticketService.getTicketByIdForEmployee(ticket.getId(), otherEmployee.getId())
        );
        assertEquals("Access denied", exception.getMessage());
    }

    // getAllTickets scenarios

    @Test
    void testGetAllTickets_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Ticket> ticketList = Collections.singletonList(ticket);
        Page<Ticket> pageTickets = new PageImpl<>(ticketList, pageable, ticketList.size());
        when(ticketRepository.findAll(pageable)).thenReturn(pageTickets);
        when(commentService.getCommentsForTicket(any(UUID.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        Page<TicketResponseDTO> result = ticketService.getAllTickets(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    // updateTicketStatus scenarios

    @Test
    void testUpdateTicketStatus_Success() {
        UpdateTicketStatusDTO updateDTO = new UpdateTicketStatusDTO();
        updateDTO.setStatus(TicketStatus.RESOLVED);

        when(userService.findById(supportUser.getId())).thenReturn(Optional.of(supportUser));
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        TicketResponseDTO result = ticketService.updateTicketStatus(ticket.getId(), supportUser.getId(), updateDTO);
        assertNotNull(result);
        verify(auditLogService).logStatusChange(ticket, supportUser, TicketStatus.NEW.name(), TicketStatus.RESOLVED.name());
    }

    @Test
    void testUpdateTicketStatus_Unauthorized() {
        // Use an employee as support user (which is unauthorized)
        when(userService.findById(employee.getId())).thenReturn(Optional.of(employee));
        UpdateTicketStatusDTO updateDTO = new UpdateTicketStatusDTO();
        updateDTO.setStatus(TicketStatus.RESOLVED);

        Exception exception = assertThrows(UnauthorizedAccessException.class, () ->
                ticketService.updateTicketStatus(ticket.getId(), employee.getId(), updateDTO)
        );
        assertEquals("Only IT support can update ticket status", exception.getMessage());
    }

    // updateTicketByEmployee scenarios

    @Test
    void testUpdateTicketByEmployee_Success() {
        UpdateTicketEmployeeDTO updateDTO = new UpdateTicketEmployeeDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setPriority("HIGH");
        updateDTO.setCategory("SOFTWARE");

        ticket.setStatus(TicketStatus.NEW);
        ticket.setCreatedBy(employee);
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        TicketResponseDTO result = ticketService.updateTicketByEmployee(ticket.getId(), employee.getId(), updateDTO);
        assertNotNull(result);
        verify(ticketRepository).save(ticket);
    }

    @Test
    void testUpdateTicketByEmployee_Unauthorized() {
        AppUser anotherEmployee = new AppUser();
        anotherEmployee.setId(UUID.randomUUID());
        ticket.setCreatedBy(employee);
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        UpdateTicketEmployeeDTO updateDTO = new UpdateTicketEmployeeDTO();
        Exception exception = assertThrows(UnauthorizedAccessException.class, () ->
                ticketService.updateTicketByEmployee(ticket.getId(), anotherEmployee.getId(), updateDTO)
        );
        assertEquals("Employee is not the creator of this ticket", exception.getMessage());
    }

    @Test
    void testUpdateTicketByEmployee_InvalidStatus() {
        UpdateTicketEmployeeDTO updateDTO = new UpdateTicketEmployeeDTO();
        updateDTO.setTitle("Updated Title");
        updateDTO.setDescription("Updated Description");
        updateDTO.setPriority("HIGH");
        updateDTO.setCategory("SOFTWARE");

        ticket.setStatus(TicketStatus.IN_PROGRESS);
        ticket.setCreatedBy(employee);
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ticketService.updateTicketByEmployee(ticket.getId(), employee.getId(), updateDTO)
        );
        assertEquals("Ticket can only be updated if its status is NEW", exception.getMessage());
    }

    // deleteTicket scenarios

    @Test
    void testDeleteTicket_Success_ForEmployee() {
        // Ticket created by employee, status NEW, no comments
        ticket.setCreatedBy(employee);
        ticket.setStatus(TicketStatus.NEW);
        ticket.setComments(new ArrayList<>());
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(userService.findById(employee.getId())).thenReturn(Optional.of(employee));

        ticketService.deleteTicket(ticket.getId(), employee.getId());

        verify(auditLogService).deleteLogsForTicket(ticket);
        verify(ticketRepository).delete(ticket);
    }

    @Test
    void testDeleteTicket_Fails_InProgress() {
        ticket.setCreatedBy(employee);
        ticket.setStatus(TicketStatus.IN_PROGRESS);
        when(ticketRepository.findById(ticket.getId())).thenReturn(Optional.of(ticket));
        when(userService.findById(employee.getId())).thenReturn(Optional.of(employee));

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                ticketService.deleteTicket(ticket.getId(), employee.getId())
        );
        assertEquals("Cannot delete a ticket that is in progress", exception.getMessage());
    }

    // searchTickets scenarios

    @Test
    void testSearchTickets_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        UUID ticketId = ticket.getId();
        String ticketIdStr = ticketId.toString();
        String statusStr = TicketStatus.NEW.name();

        List<Ticket> ticketList = Collections.singletonList(ticket);
        Page<Ticket> pageTickets = new PageImpl<>(ticketList, pageable, ticketList.size());
        // Cast repository and use any(Specification.class) matcher.
        when(((JpaSpecificationExecutor<Ticket>) ticketRepository)
                .findAll(any(Specification.class), eq(pageable)))
                .thenReturn(pageTickets);

        Page<TicketResponseDTO> result = ticketService.searchTickets(ticketIdStr, statusStr, pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testSearchTickets_NotFound() {
        Pageable pageable = PageRequest.of(0, 10);
        when(((JpaSpecificationExecutor<Ticket>) ticketRepository)
                .findAll(any(Specification.class), eq(pageable)))
                .thenReturn(Page.empty());
        Exception exception = assertThrows(TicketNotFoundException.class, () ->
                ticketService.searchTickets("", "", pageable)
        );
        assertEquals("No tickets found with the provided criteria", exception.getMessage());
    }
}
