package com.cram.backend.testcontroller;

import com.cram.backend.jwt.JWTUtil;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Tag(name = "개발용 토큰 발급 컨트롤러",
        description =
                "email은 user1@example.com ~ user10 까지 DB에 있습니다." +
                "user1~5는 1번그룹, user4~8는 2번그룹, user9는 1,2,3번 그룹에 가입되어 있습니다." +
                "user1은 1번그룹, 5는 2번그룹, 9는 3번 그룹의 리더입니다.")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ChatTestController {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;


    // GET /api/token?email=user1@example.com
    @Operation(summary = "토큰 발급", description = "user1@example.com ~ user10@example.com")
    @GetMapping("/token")
    public ResponseEntity<?> generateToken(@RequestParam String email) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        UserEntity user = userOpt.get();

        String token = jwtUtil.createJwt("access", user.getId(), user.getUsername(), user.getRole(), 6999990L);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "티켓 발급", description = "user1@example.com ~ user10@example.com")
    @GetMapping("/ticket")
    public ResponseEntity<?> generateTicket(@RequestParam String email, @RequestParam(defaultValue = "1") Long groupId) {
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        UserEntity user = userOpt.get();

        String token = jwtUtil.createJwt("access", user.getId(), user.getUsername(), user.getRole(), 6999990L);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("groupId", groupId); // 기본 그룹 ID 반환

        return ResponseEntity.ok(response);
    }
}