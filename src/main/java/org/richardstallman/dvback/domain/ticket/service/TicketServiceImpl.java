package org.richardstallman.dvback.domain.ticket.service;

import static org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType.CHARGE;
import static org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType.USE;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.common.constant.CommonConstants.TicketTransactionType;
import org.richardstallman.dvback.domain.ticket.converter.TicketConverter;
import org.richardstallman.dvback.domain.ticket.converter.TicketTransactionConverter;
import org.richardstallman.dvback.domain.ticket.domain.TicketDomain;
import org.richardstallman.dvback.domain.ticket.domain.TicketTransactionDomain;
import org.richardstallman.dvback.domain.ticket.domain.TicketUserInfoDto;
import org.richardstallman.dvback.domain.ticket.domain.request.TicketTransactionRequestDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketResponseDto;
import org.richardstallman.dvback.domain.ticket.domain.response.TicketTransactionDetailResponseDto;
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

    UserDomain userDomain = getUser(userId);

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
                getUserTicket(userDomain),
                ticketTransactionRequestDto.amount(),
                ticketTransactionRequestDto.interviewAssetType(),
                ticketTransactionRequestDto.interviewMode()));

    return ticketTransactionConverter.fromDomainToResponseDto(
        ticketTransactionDomain, ticketDomain);
  }

  @Override
  @Transactional
  public TicketResponseDto useTicket(
      TicketTransactionRequestDto ticketTransactionRequestDto, Long userId) {
    validateTransactionType(ticketTransactionRequestDto.ticketTransactionType(), USE);

    UserDomain userDomain = getUser(userId);
    TicketDomain ticketDomain = getUserTicket(userDomain);

    TicketTransactionDomain ticketTransactionDomain =
        ticketTransactionRepository.save(
            ticketTransactionConverter.fromRequestDtoToDomain(
                ticketTransactionRequestDto,
                userDomain,
                generateDescription(ticketTransactionRequestDto),
                getCurrentDateTime()));

    ticketDomain =
        ticketRepository.save(
            ticketConverter.updateBalance(
                ticketDomain,
                ticketTransactionRequestDto.amount(),
                ticketTransactionRequestDto.interviewAssetType(),
                ticketTransactionRequestDto.interviewMode()));
    return new TicketResponseDto(
        ticketDomain.getRealChatBalance()
            + ticketDomain.getRealVoiceBalance()
            + ticketDomain.getGeneralChatBalance()
            + ticketDomain.getGeneralVoiceBalance(),
        ticketDomain.getRealChatBalance(),
        ticketDomain.getRealVoiceBalance(),
        ticketDomain.getGeneralChatBalance(),
        ticketDomain.getGeneralVoiceBalance(),
        ticketTransactionConverter.fromDomainToDetailResponseDto(ticketTransactionDomain));
  }

  @Override
  public TicketUserInfoDto getUserTicketInfo(Long userId) {
    UserDomain userDomain = getUser(userId);
    TicketDomain ticketDomain = getUserTicket(userDomain);
    int currentBalance =
        ticketDomain.getRealChatBalance()
            + ticketDomain.getRealVoiceBalance()
            + ticketDomain.getGeneralChatBalance()
            + ticketDomain.getGeneralVoiceBalance();
    List<TicketTransactionDetailResponseDto> ticketTransactionDetailResponseDtos =
        ticketTransactionRepository.findTicketsByUserId(userId).stream()
            .map(ticketTransactionConverter::fromDomainToDetailResponseDto)
            .toList();
    return new TicketUserInfoDto(
        currentBalance,
        ticketDomain.getRealChatBalance(),
        ticketDomain.getRealVoiceBalance(),
        ticketDomain.getGeneralChatBalance(),
        ticketDomain.getGeneralVoiceBalance(),
        ticketTransactionDetailResponseDtos);
  }

  @Override
  public int getUserRealChatTicketCount(Long userId) {
    UserDomain userDomain = getUser(userId);
    return getUserTicket(userDomain).getRealChatBalance();
  }

  @Override
  public int getUserRealVoiceTicketCount(Long userId) {
    UserDomain userDomain = getUser(userId);
    return getUserTicket(userDomain).getRealVoiceBalance();
  }

  @Override
  public int getUserGeneralChatTicketCount(Long userId) {
    UserDomain userDomain = getUser(userId);
    return getUserTicket(userDomain).getGeneralChatBalance();
  }

  @Override
  public int getUserGeneralVoiceTicketCount(Long userId) {
    UserDomain userDomain = getUser(userId);
    return getUserTicket(userDomain).getGeneralVoiceBalance();
  }

  private UserDomain getUser(Long userId) {
    return userRepository
        .findById(userId)
        .orElseThrow(
            () -> new ApiException(HttpStatus.NOT_FOUND, "(" + userId + "): User Not Found"));
  }

  private TicketDomain getUserTicket(UserDomain userDomain) {
    TicketDomain ticketDomain = ticketRepository.findByUserId(userDomain.getId());
    if (ticketDomain == null) {
      ticketDomain =
          ticketRepository.save(
              TicketDomain.builder()
                  .userDomain(userDomain)
                  .realChatBalance(0)
                  .realVoiceBalance(0)
                  .generalChatBalance(0)
                  .generalVoiceBalance(0)
                  .build());
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
