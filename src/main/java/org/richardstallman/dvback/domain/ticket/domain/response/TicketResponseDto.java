package org.richardstallman.dvback.domain.ticket.domain.response;

import jakarta.validation.constraints.NotNull;

public record TicketResponseDto(
    @NotNull int currentBalance,
    @NotNull int currentChatBalance,
    @NotNull int currentVoiceBalance,
    @NotNull TicketTransactionDetailResponseDto ticketTransactionDetail) {}
