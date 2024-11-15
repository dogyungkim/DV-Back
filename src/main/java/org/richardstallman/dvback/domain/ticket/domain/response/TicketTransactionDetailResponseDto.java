package org.richardstallman.dvback.domain.ticket.domain.response;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionMethod;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketType;

public record TicketTransactionDetailResponseDto(
    @NotNull Long ticketTransactionId,
    @NotNull int amount,
    @NotNull TicketTransactionType ticketTransactionType,
    @NotNull String ticketTransactionTypeKorean,
    @NotNull TicketTransactionMethod ticketTransactionMethod,
    @NotNull String ticketTransactionMethodKorean,
    @NotNull TicketType ticketType,
    @NotNull String ticketTypeKorean,
    @NotNull String description,
    @NotNull LocalDateTime generatedAt) {}
