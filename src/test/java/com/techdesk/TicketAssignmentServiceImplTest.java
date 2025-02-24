package com.techdesk;

import com.techdesk.entities.AppUser;
import com.techdesk.entities.Ticket;
import com.techdesk.entities.enums.Role;
import com.techdesk.entities.enums.TicketStatus;
import com.techdesk.repositories.AppUserRepository;
import com.techdesk.repositories.TicketRepository;
import com.techdesk.services.Impl.TicketAssignmentServiceImpl;
import com.techdesk.web.errors.NoSupportAgentAvailableException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TicketAssignmentServiceImplTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private TicketAssignmentServiceImpl ticketAssignmentService;

    private Ticket ticket;
    private AppUser agent1;
    private AppUser agent2;

    @BeforeEach
    void setUp() {
        ticket = new Ticket();
        ticket.setId(UUID.randomUUID());

        agent1 = new AppUser();
        agent1.setId(UUID.randomUUID());
        agent1.setUsername("agent1");
        agent1.setRole(Role.IT_SUPPORT);

        agent2 = new AppUser();
        agent2.setId(UUID.randomUUID());
        agent2.setUsername("agent2");
        agent2.setRole(Role.IT_SUPPORT);
    }

    @Test
    void assignTicket_LeastLoadedAgent_ReturnsAgent() {
        List<AppUser> supportAgents = Arrays.asList(agent1, agent2);
        when(appUserRepository.findByRole(Role.IT_SUPPORT)).thenReturn(supportAgents);
        when(ticketRepository.countByAssignedToAndStatusNotIn(agent1, Arrays.asList(TicketStatus.RESOLVED, TicketStatus.CLOSED)))
                .thenReturn(2);
        when(ticketRepository.countByAssignedToAndStatusNotIn(agent2, Arrays.asList(TicketStatus.RESOLVED, TicketStatus.CLOSED)))
                .thenReturn(1);

        AppUser assignedAgent = ticketAssignmentService.assignTicket(ticket);

        assertEquals(agent2, assignedAgent, "Ticket should be assigned to the least loaded agent");
        verify(appUserRepository, times(1)).findByRole(Role.IT_SUPPORT);
        verify(ticketRepository, times(2)).countByAssignedToAndStatusNotIn(any(), any());
    }

    @Test
    void assignTicket_NoAgentsAvailable_ThrowsException() {
        when(appUserRepository.findByRole(Role.IT_SUPPORT)).thenReturn(Collections.emptyList());


        assertThrows(NoSupportAgentAvailableException.class, () -> ticketAssignmentService.assignTicket(ticket),
                "No support agents available should throw NoSupportAgentAvailableException");
        verify(appUserRepository, times(1)).findByRole(Role.IT_SUPPORT);
        verify(ticketRepository, never()).countByAssignedToAndStatusNotIn(any(), any());
    }

    @Test
    void assignTicket_AllAgentsSameWorkload_ReturnsOneAgent() {
        List<AppUser> supportAgents = Arrays.asList(agent1, agent2);
        when(appUserRepository.findByRole(Role.IT_SUPPORT)).thenReturn(supportAgents);
        when(ticketRepository.countByAssignedToAndStatusNotIn(any(), any())).thenReturn(2);

        AppUser assignedAgent = ticketAssignmentService.assignTicket(ticket);

        assertNotNull(assignedAgent, "Ticket should be assigned to one of the agents");
        assertTrue(supportAgents.contains(assignedAgent), "Assigned agent should be one of the available agents");
        verify(appUserRepository, times(1)).findByRole(Role.IT_SUPPORT);
        verify(ticketRepository, times(2)).countByAssignedToAndStatusNotIn(any(), any());
    }

    @Test
    void assignTicket_RepositoryThrowsException_PropagatesException() {
        when(appUserRepository.findByRole(Role.IT_SUPPORT)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(DataAccessException.class, () -> ticketAssignmentService.assignTicket(ticket),
                "Repository exception should be propagated");
        verify(appUserRepository, times(1)).findByRole(Role.IT_SUPPORT);
        verify(ticketRepository, never()).countByAssignedToAndStatusNotIn(any(), any());
    }


}