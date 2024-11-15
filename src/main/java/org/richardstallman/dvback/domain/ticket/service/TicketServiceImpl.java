package org.richardstallman.dvback.domain.ticket.service;

import static org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType.CHARGE;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.ticket.converter.TicketConverter;
import org.richardstallman.dvback.domain.ticket.converter.TicketTransactionConverter;
import org.richardstallman.dvback.domain.ticket.domain.TicketDomain;
import org.richardstallman.dvback.domain.ticket.domain.TicketTransactionDomain;
import org.richardstallman.dvback.domain.ticket.domain.request.TicketTransactionRequestDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionResponseDto;
import org.richardstallman.dvback.domain.ticket.repository.TicketRepository;
import org.richardstallman.dvback.domain.ticket.repository.transaction.TicketTransactionRepository;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.richardstallman.dvback.global.advice.exceptions.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

  private final TicketRepository ticketRepository;
  private final TicketTransactionRepository ticketTransactionRepository;
  private final TicketConverter ticketConverter;
  private final TicketTransactionConverter ticketTransactionConverter;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public TicketTransactionResponseDto chargeTicket(
      TicketTransactionRequestDto ticketTransactionRequestDto, Long userId) {
    validateTransactionType(ticketTransactionRequestDto.ticketTransactionType(), CHARGE);

    UserDomain userDomain =
        userRepository
            .findById(userId)
            .orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, "(" + userId + "): User Not Found"));

    TicketTransactionDomain ticketTransactionDomain =
        ticketTransactionRepository.save(
            ticketTransactionConverter.fromRequestDtoToDomain(
                ticketTransactionRequestDto,
                userDomain,
                generateDescription(ticketTransactionRequestDto),
                getCurrentDateTime()));

    TicketDomain ticketDomain =
        ticketRepository.save(
            ticketConverter.updateBalance(
                getUserTicket(userDomain), ticketTransactionRequestDto.amount()));
    return ticketTransactionConverter.fromDomainToResponseDto(
        ticketTransactionDomain, ticketDomain);
  }

  private TicketDomain getUserTicket(UserDomain userDomain) {
    TicketDomain ticketDomain = ticketRepository.findByUserId(userDomain.getId());
    if (ticketDomain == null) {
      ticketDomain =
          ticketRepository.save(TicketDomain.builder().userDomain(userDomain).balance(0).build());
    }
    return ticketDomain;
  }

  private String generateDescription(TicketTransactionRequestDto ticketTransactionRequestDto) {
    if (ticketTransactionRequestDto.description() == null
        || ticketTransactionRequestDto.description().isEmpty()) {
      return ticketTransactionRequestDto.ticketTransactionMethod().getKoreanName()
          + " "
          + ticketTransactionRequestDto.ticketTransactionType().getKoreanName();
    }
    return ticketTransactionRequestDto.description();
  }

  private void validateTransactionType(
      @NotNull TicketTransactionType actual, TicketTransactionType expected) {
    if (actual != expected) {
      throw new ApiException(
          HttpStatus.BAD_REQUEST,
          "Invalid transaction type: expected (" + expected + "), actual (" + actual + ")");
    }
  }

  private LocalDateTime getCurrentDateTime() {
    return LocalDateTime.now();
  }
}
