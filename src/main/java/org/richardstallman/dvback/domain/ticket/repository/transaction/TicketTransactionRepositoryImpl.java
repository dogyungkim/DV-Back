package org.richardstallman.dvback.domain.ticket.repository.transaction;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.ticket.converter.TicketTransactionConverter;
import org.richardstallman.dvback.domain.ticket.domain.TicketTransactionDomain;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class TicketTransactionRepositoryImpl implements TicketTransactionRepository {

  private final TicketTransactionJpaRepository ticketTransactionJpaRepository;
  private final TicketTransactionConverter ticketTransactionConverter;

  @Override
  public TicketTransactionDomain save(TicketTransactionDomain transaction) {
    return ticketTransactionConverter.fromEntityToDomain(
        ticketTransactionJpaRepository.save(
            ticketTransactionConverter.fromDomainToEntity(transaction)));
  }
}
