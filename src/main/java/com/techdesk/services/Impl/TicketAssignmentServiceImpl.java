package com.techdesk.services.Impl;

import com.techdesk.entities.AppUser;
import com.techdesk.entities.Ticket;
import com.techdesk.entities.enums.Role;
import com.techdesk.entities.enums.TicketStatus;
import com.techdesk.repositories.AppUserRepository;
import com.techdesk.repositories.TicketRepository;
import com.techdesk.services.TicketAssignmentService;
import com.techdesk.web.errors.NoSupportAgentAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TicketAssignmentServiceImpl implements TicketAssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(TicketAssignmentServiceImpl.class);

    private final AppUserRepository appUserRepository;
    private final TicketRepository ticketRepository;

    public TicketAssignmentServiceImpl(AppUserRepository appUserRepository, TicketRepository ticketRepository) {
        this.appUserRepository = appUserRepository;
        this.ticketRepository = ticketRepository;
    }

    @Override
    public AppUser assignTicket(Ticket ticket) {
        List<AppUser> supportAgents = appUserRepository.findByRole(Role.IT_SUPPORT);
        if (supportAgents.isEmpty()) {
            throw new NoSupportAgentAvailableException("No IT support agents available for assignment");
        }

        List<TicketStatus> closedStatuses = Arrays.asList(TicketStatus.RESOLVED, TicketStatus.CLOSED);
        AppUser leastLoadedAgent = null;
        int minTicketCount = Integer.MAX_VALUE;

        for (AppUser agent : supportAgents) {
            int openTicketCount = ticketRepository.countByAssignedToAndStatusNotIn(agent, closedStatuses);
            if (openTicketCount < minTicketCount) {
                minTicketCount = openTicketCount;
                leastLoadedAgent = agent;
            }
        }

        if (leastLoadedAgent == null) {
            throw new NoSupportAgentAvailableException("No available support agent found");
        }

        logger.info("Assigning ticket {} to support agent {} with {} open tickets",
                ticket.getId(), leastLoadedAgent.getUsername(), minTicketCount);
        return leastLoadedAgent;
    }
}
