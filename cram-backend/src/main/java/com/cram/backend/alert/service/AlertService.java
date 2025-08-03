package com.cram.backend.alert.service;

import com.cram.backend.alert.dto.DeleteAlertResponseDto;
import com.cram.backend.alert.dto.GetAlertListResponseDto;
import com.cram.backend.alert.dto.ReadAllAlertsResponseDto;
import com.cram.backend.alert.dto.ReadAlertResponseDto;

public interface AlertService {

    /**
     * 사용자의 최근 2주 내 알림 목록 조회
     */
    GetAlertListResponseDto getAlertList(Long userId);

    /**
     * 특정 알림 읽음 처리
     */
    ReadAlertResponseDto readAlert(Long userId, Long alertId);

    /**
     * 모든 알림 읽음 처리
     */
    ReadAllAlertsResponseDto readAllAlerts(Long userId);

    /**
     * 특정 알림 삭제
     */
    DeleteAlertResponseDto deleteAlert(Long userId, Long alertId);
}