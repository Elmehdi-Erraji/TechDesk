package com.techdesk.dto.mappers;


import com.techdesk.dto.CreateTicketDTO;
import com.techdesk.dto.TicketResponseDTO;
import com.techdesk.entities.Ticket;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TicketMapper {
    Ticket createTicketDTOToTicket(CreateTicketDTO dto);
    TicketResponseDTO ticketToTicketResponseDTO(Ticket ticket);
}