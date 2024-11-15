package org.richardstallman.dvback.domain.ticket.repository.transaction;

import org.richardstallman.dvback.domain.ticket.domain.TicketTransactionDomain;

public interface TicketTransactionRepository {

  TicketTransactionDomain save(TicketTransactionDomain transaction);
}
