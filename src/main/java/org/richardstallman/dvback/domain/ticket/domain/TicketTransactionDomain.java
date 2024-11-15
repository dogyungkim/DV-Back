package org.richardstallman.dvback.domain.ticket.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketType;
import org.richardstallman.dvback.domain.user.domain.UserDomain;

@Getter
@Builder
public class TicketTransactionDomain {

  private final Long ticketTransactionId;
  private final UserDomain userDomain;
  private int amount;
  private TicketTransactionType ticketTransactionType;
  private TicketTransactionMethod ticketTransactionMethod;
  private TicketType ticketType;
  private String description;
  private LocalDateTime generatedAt;
}
