package com.cram.backend.alert.service;

import com.cram.backend.alert.dto.DeleteAlertResponseDto;
import com.cram.backend.alert.dto.GetAlertListResponseDto;
import com.cram.backend.alert.dto.ReadAllAlertsResponseDto;
import com.cram.backend.alert.dto.ReadAlertResponseDto;
import com.cram.backend.alert.entity.Alert;
import com.cram.backend.alert.entity.GeneralAlert;
import com.cram.backend.alert.entity.GroupAlert;
import com.cram.backend.alert.repository.AlertRepository;
import com.cram.backend.alert.repository.GeneralAlertRepository;
import com.cram.backend.alert.repository.GroupAlertRepository;
import com.cram.backend.user.entity.UserEntity;
import com.cram.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final GeneralAlertRepository generalAlertRepository;
    private final GroupAlertRepository groupAlertRepository;
    private final UserRepository userRepository;

    @Override
    public GetAlertListResponseDto getAlertList(Long userId) {
        log.info("Getting alert list for user: {}", userId);

        // 1. 사용자 존재 여부 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. User ID: " + userId));

        // 2. 최근 2주 내 알림 조회
        LocalDateTime twoWeeksAgo = LocalDateTime.now().minusWeeks(2);
        Pageable pageable = PageRequest.of(0, 50); // 최대 50개

        Page<Alert> alerts = alertRepository
                .findRecentAlertsByUserId(userId, twoWeeksAgo, pageable);

        // 3. Entity를 DTO로 변환
        List<GetAlertListResponseDto.AlertDto> alertDtos =
                alerts.getContent().stream()
                        .map(this::convertToAlertDto)
                        .collect(Collectors.toList());

        // 4. 응답 DTO 생성
        return GetAlertListResponseDto.builder()
                .data(alertDtos)
                .build();
    }

    @Override
    @Transactional
    public ReadAlertResponseDto readAlert(Long userId, Long alertId) {
        log.info("Reading alert: {} for user: {}", alertId, userId);

        // 1. 알림 존재 여부 및 권한 확인
        Alert alert = alertRepository.findByIdAndUserId(alertId, userId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없거나 권한이 없습니다. " +
                        "Alert ID: " + alertId + ", User ID: " + userId));

        // 2. 이미 읽은 알림인지 확인
        if (alert.getIsRead()) {
            log.warn("Alert {} is already read", alertId);
        }

        // 3. 읽음 처리
        alert.markAsRead();
        Alert savedAlert = alertRepository.save(alert);

        // 4. 응답 DTO 생성
        return ReadAlertResponseDto.builder()
                .alertId(savedAlert.getId())
                .isRead(savedAlert.getIsRead())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public ReadAllAlertsResponseDto readAllAlerts(Long userId) {
        log.info("Reading all alerts for user: {}", userId);

        // 1. 사용자 존재 여부 확인
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다. User ID: " + userId));

        // 2. 모든 안읽은 알림을 읽음 처리
        int updatedCount = alertRepository.markAllAsReadByUserId(userId);

        log.info("Marked {} alerts as read for user: {}", updatedCount, userId);

        // 3. 응답 DTO 생성
        return ReadAllAlertsResponseDto.builder()
                .updatedCount(updatedCount)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public DeleteAlertResponseDto deleteAlert(Long userId, Long alertId) {
        log.info("Deleting alert: {} for user: {}", alertId, userId);

        // 1. 알림 존재 여부 및 권한 확인
        Alert alert = alertRepository.findByIdAndUserId(alertId, userId)
                .orElseThrow(() -> new RuntimeException("알림을 찾을 수 없거나 권한이 없습니다. " +
                        "Alert ID: " + alertId + ", User ID: " + userId));

        // 2. 알림 삭제
        alertRepository.delete(alert);

        log.info("Successfully deleted alert: {}", alertId);

        // 3. 응답 DTO 생성
        return DeleteAlertResponseDto.builder()
                .alertId(alertId)
                .deletedAt(LocalDateTime.now())
                .build();
    }

    /**
     * Alert Entity를 AlertDto로 변환하는 private 메서드
     */
    private GetAlertListResponseDto.AlertDto convertToAlertDto(Alert alert) {
        String type = "";
        Long targetId = null;
        String targetType = "";
        GetAlertListResponseDto.SenderInfo senderInfo = null;

        // 카테고리에 따라 세부 정보 조회
        if (alert.isGroupAlert()) {
            Optional<GroupAlert> groupAlert = groupAlertRepository.findByAlert(alert);
            if (groupAlert.isPresent()) {
                type = groupAlert.get().getCategory().name();
                targetId = groupAlert.get().getStudyGroup().getId();
                targetType = "group";
                // 그룹 알림의 경우 발신자 정보는 보통 시스템이므로 null
            }
        } else if (alert.isGeneralAlert()) {
            Optional<GeneralAlert> generalAlert = generalAlertRepository.findByAlert(alert);
            if (generalAlert.isPresent()) {
                type = generalAlert.get().getCategory().name();
                targetType = "inquiry";
                // 일반 알림의 경우도 시스템 알림이므로 발신자 정보는 null
            }
        }

        // AlertDto 생성
        return GetAlertListResponseDto.AlertDto.builder()
                .alertId(alert.getId())
                .category(alert.getCategory().name())
                .type(type)
                .message(alert.getMessage())
                .targetId(targetId)
                .targetType(targetType)
                .sender(senderInfo)
                .isRead(alert.getIsRead())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}