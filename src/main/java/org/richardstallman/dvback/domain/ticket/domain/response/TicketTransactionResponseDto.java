package org.richardstallman.dvback.domain.ticket.domain.response;

import jakarta.validation.constraints.NotNull;

public record TicketTransactionResponseDto(
    @NotNull int currentBalance,
    @NotNull int currentChatBalance,
    @NotNull int currentVoiceBalance,
    @NotNull TicketTransactionDetailResponseDto ticketTransactionDetail) {}
