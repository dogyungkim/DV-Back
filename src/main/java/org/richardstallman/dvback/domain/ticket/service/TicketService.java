package org.richardstallman.dvback.domain.ticket.service;

import org.richardstallman.dvback.domain.ticket.domain.request.TicketTransactionRequestDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;

public interface TicketService {

  TicketTransactionResponseDto chargeTicket(
      TicketTransactionRequestDto ticketTransactionRequestDto, Long userId);
}
