package org.richardstallman.dvback.domain.user.service;

import lombok.extern.slf4j.Slf4j;
import org.richardstallman.dvback.domain.user.converter.UserConverter;
import org.richardstallman.dvback.domain.user.domain.UserDomain;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.richardstallman.dvback.domain.user.domain.request.UserRequestDto;
import org.richardstallman.dvback.domain.user.domain.response.UserResponseDto;
import org.richardstallman.dvback.domain.user.entity.UserEntity;
import org.richardstallman.dvback.domain.user.repository.UserRepository;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserConverter userConverter;

    @Transactional
    public UserResponseDto updateUserInfo(Long userId, UserRequestDto userRequestDto) {
        log.info("updateUserInfo");
        UserDomain user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));
        UserEntity userEntity = userConverter.fromDomainToEntity(user);

        UserEntity updatedUser = userEntity.updatedUserEntity(
                userRequestDto.nickname(),
                userRequestDto.birthdate(),
                userRequestDto.gender()
        );

        userRepository.save(userConverter.fromEntityToDomain(updatedUser));

        return new UserResponseDto(
                updatedUser.getId(),
                updatedUser.getSocialId(),
                updatedUser.getEmail(),
                updatedUser.getName(),
                updatedUser.getNickname(),
                updatedUser.getS3ProfileImageUrl(),
                updatedUser.getLeave(),
                updatedUser.getGender(),
                updatedUser.getBirthdate()
        );
    }
}