package com.techdesk.services;

import com.techdesk.entities.AppUser;
import com.techdesk.entities.Ticket;

public interface TicketAssignmentService {

    AppUser assignTicket(Ticket ticket);

}
