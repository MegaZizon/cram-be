package com.cram.backend.user.controller;

import com.cram.backend.user.dto.CustomOAuth2User;
import com.cram.backend.user.dto.UserDTO;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getMyInfo(Authentication authentication) {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        Long userId = user.getUserId();

        UserEntity entity = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(entity.getId());
        userDTO.setRole(entity.getRole());
        userDTO.setName(entity.getName());
        userDTO.setEmail(entity.getEmail());
        userDTO.setProfileImage(entity.getProfileImage());
        userDTO.setBirthDate(entity.getBirthDate());
        userDTO.setPhone(entity.getPhone());

        return ResponseEntity.ok(userDTO);
    }
}
