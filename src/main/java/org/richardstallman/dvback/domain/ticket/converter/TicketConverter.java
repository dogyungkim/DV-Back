package org.richardstallman.dvback.domain.ticket.converter;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.ticket.domain.TicketDomain;
import org.richardstallman.dvback.domain.ticket.entity.TicketEntity;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketConverter {

  private final UserConverter userConverter;

  public TicketDomain fromEntityToDomain(TicketEntity ticketEntity) {
    return TicketDomain.builder()
        .ticketId(ticketEntity.getTicketId())
        .userDomain(userConverter.fromEntityToDomain(ticketEntity.getUser()))
        .balance(ticketEntity.getBalance())
        .build();
  }

  public TicketEntity fromDomainToEntity(TicketDomain ticketDomain) {
    return new TicketEntity(
        ticketDomain.getTicketId(),
        userConverter.fromDomainToEntity(ticketDomain.getUserDomain()),
        ticketDomain.getBalance());
  }

  public TicketDomain updateBalance(TicketDomain ticketDomain, int amount) {
    return TicketDomain.builder()
        .ticketId(ticketDomain.getTicketId())
        .userDomain(ticketDomain.getUserDomain())
        .balance(ticketDomain.getBalance() + amount)
        .build();
  }
}
