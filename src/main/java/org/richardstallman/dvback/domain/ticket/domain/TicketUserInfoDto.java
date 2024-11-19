package org.richardstallman.dvback.domain.ticket.domain;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionDetailResponseDto;

public record TicketUserInfoDto(
    @NotNull int totalBalance,
    @NotNull int realChatBalance,
    @NotNull int realVoiceBalance,
    @NotNull int generalChatBalance,
    @NotNull int generalVoiceBalance,
    @NotNull List<TicketTransactionDetailResponseDto> ticketTransactionDetails) {}
