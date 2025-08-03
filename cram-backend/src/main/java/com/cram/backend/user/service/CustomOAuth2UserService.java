package com.cram.backend.user.service;

import com.cram.backend.user.dto.*;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    public CustomOAuth2UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        System.out.printf("UserService: %s%n", oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response;

        if (registrationId.equals("naver")) {
            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        } else if (registrationId.equals("google")) {
            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        } else {
            return null;
        }

        // 통합 username
        String username = oAuth2Response.getProvider() + " " + oAuth2Response.getProviderId();

        // 생일 파싱
        LocalDate birthDate = null;
        if (oAuth2Response instanceof HasBirthday birthdayInfo) {
            try {
                String year = birthdayInfo.getBirthYear();
                String dayMonth = birthdayInfo.getBirthday();
                birthDate = LocalDate.parse(year + "-" + dayMonth, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (DateTimeParseException ignored) {}
        }

        // 사용자 조회 (Optional 사용 안 하고 if-else 처리 유지)
        UserEntity userEntity = userRepository.findByUsername(username).orElse(null);

        if (userEntity == null) {
            // 신규 회원이면 저장
            userEntity = new UserEntity();
            userEntity.setProvider(oAuth2Response.getProvider());
            userEntity.setProviderId(oAuth2Response.getProviderId());
            userEntity.setUsername(username);
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            userEntity.setProfileImage(oAuth2Response.getProfileImage());
            userEntity.setPhone(oAuth2Response.getPhone());
            userEntity.setBirthDate(birthDate);
            userEntity.setRole("ROLE_USER");
        } else {
            // 기존 회원이면 정보 업데이트
            userEntity.setEmail(oAuth2Response.getEmail());
            userEntity.setName(oAuth2Response.getName());
            userEntity.setProfileImage(oAuth2Response.getProfileImage());
            userEntity.setPhone(oAuth2Response.getPhone());
            userEntity.setBirthDate(birthDate);
        }

        // DB 저장
        userEntity = userRepository.save(userEntity);

        // DTO 생성
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userEntity.getId());
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setName(userEntity.getName());
        userDTO.setProfileImage(userEntity.getProfileImage());
        userDTO.setPhone(userEntity.getPhone());
        userDTO.setBirthDate(userEntity.getBirthDate());
        userDTO.setRole(userEntity.getRole());

        return new CustomOAuth2User(userDTO);
    }
}
