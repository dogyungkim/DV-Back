package org.richardstallman.dvback.domain.ticket.converter;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.ticket.domain.TicketDomain;
import org.richardstallman.dvback.domain.ticket.domain.TicketTransactionDomain;
import org.richardstallman.dvback.domain.ticket.domain.request.TicketTransactionRequestDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionDetailResponseDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;
import org.richardstallman.dvback.domain.ticket.entity.TicketTransactionEntity;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TicketTransactionConverter {

  private final UserConverter userConverter;

  public TicketTransactionDomain fromEntityToDomain(
      TicketTransactionEntity ticketTransactionEntity) {
    return TicketTransactionDomain.builder()
        .amount(ticketTransactionEntity.getAmount())
        .ticketTransactionId(ticketTransactionEntity.getTicketTransactionId())
        .userDomain(userConverter.fromEntityToDomain(ticketTransactionEntity.getUser()))
        .ticketTransactionType(ticketTransactionEntity.getTicketTransactionType())
        .ticketTransactionMethod(ticketTransactionEntity.getTicketTransactionMethod())
        .description(ticketTransactionEntity.getDescription())
        .generatedAt(ticketTransactionEntity.getGeneratedAt())
        .build();
  }

  public TicketTransactionEntity fromDomainToEntity(
      TicketTransactionDomain ticketTransactionDomain) {
    return new TicketTransactionEntity(
        ticketTransactionDomain.getTicketTransactionId(),
        userConverter.fromDomainToEntity(ticketTransactionDomain.getUserDomain()),
        ticketTransactionDomain.getAmount(),
        ticketTransactionDomain.getTicketTransactionType(),
        ticketTransactionDomain.getTicketTransactionMethod(),
        ticketTransactionDomain.getDescription(),
        ticketTransactionDomain.getGeneratedAt());
  }

  public TicketTransactionDomain fromRequestDtoToDomain(
      TicketTransactionRequestDto ticketTransactionRequestDto,
      UserDomain userDomain,
      String ticketTransactionDescription,
      LocalDateTime generatedAt) {
    return TicketTransactionDomain.builder()
        .userDomain(userDomain)
        .amount(ticketTransactionRequestDto.amount())
        .ticketTransactionType(ticketTransactionRequestDto.ticketTransactionType())
        .ticketTransactionMethod(ticketTransactionRequestDto.ticketTransactionMethod())
        .description(ticketTransactionDescription)
        .generatedAt(generatedAt)
        .build();
  }

  public TicketTransactionResponseDto fromDomainToResponseDto(
      TicketTransactionDomain ticketTransactionDomain, TicketDomain ticketDomain) {
    return new TicketTransactionResponseDto(
        ticketDomain.getBalance(),
        new TicketTransactionDetailResponseDto(
            ticketTransactionDomain.getTicketTransactionId(),
            ticketTransactionDomain.getAmount(),
            ticketTransactionDomain.getTicketTransactionType(),
            ticketTransactionDomain.getTicketTransactionType().getKoreanName(),
            ticketTransactionDomain.getTicketTransactionMethod(),
            ticketTransactionDomain.getTicketTransactionMethod().getKoreanName(),
            ticketTransactionDomain.getDescription(),
            ticketTransactionDomain.getGeneratedAt()));
  }
}
