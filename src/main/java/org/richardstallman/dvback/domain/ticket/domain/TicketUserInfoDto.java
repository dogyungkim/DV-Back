package org.richardstallman.dvback.domain.ticket.domain;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionDetailResponseDto;

public record TicketUserInfoDto(
    @NotNull int currentBalance,
    @NotNull int currentChatBalance,
    @NotNull int currentVoiceBalance,
    @NotNull List<TicketTransactionDetailResponseDto> ticketTransactionDetails) {}
