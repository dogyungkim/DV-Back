package org.richardstallman.dvback.domain.ticket.domain;

import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

@Getter
@Builder
public class TicketDomain {

  private final Long ticketId;
  private final UserDomain userDomain;
  private int balance;
}
