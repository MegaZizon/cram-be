package com.cram.backend.alert.controller;

import com.cram.backend.alert.dto.DeleteAlertResponseDto;
import com.cram.backend.alert.dto.GetAlertListResponseDto;
import com.cram.backend.alert.dto.ReadAllAlertsResponseDto;
import com.cram.backend.alert.dto.ReadAlertResponseDto;
import com.cram.backend.alert.service.AlertService;
import com.cram.backend.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Alert API", description = "알림 관리 API")
public class AlertController {

    private final AlertService alertService;
    private final JWTUtil jwtUtil;

    @Operation(summary = "알림 목록 조회", description = "사용자가 글로벌 네비게이션 바의 알림 아이콘을 클릭하여, 최근 2주 내 발생한 본인의 알림 목록을 조회할 수 있습니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping
    public ResponseEntity<GetAlertListResponseDto> getAlertList(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        Long userId = jwtUtil.getUserId(token);

        return ResponseEntity.ok(alertService.getAlertList(userId));
    }

    @Operation(summary = "알림을 읽음 상태로 변경", description = "특정 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 알림"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음")
    })
    @PatchMapping("/{alert_id}/read")
    public ResponseEntity<ReadAlertResponseDto> readAlert(
            @Parameter(description = "알림 ID", example = "12345")
            @PathVariable("alert_id") Long alertId,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        Long userId = jwtUtil.getUserId(token);

        ReadAlertResponseDto response = alertService.readAlert(userId, alertId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "알림 읽음 일괄 처리", description = "사용자의 모든 안읽은 알림을 읽음 상태로 일괄 변경합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PatchMapping("/read-all")
    public ResponseEntity<ReadAllAlertsResponseDto> readAllAlerts(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        Long userId = jwtUtil.getUserId(token);

        ReadAllAlertsResponseDto response = alertService.readAllAlerts(userId);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "403", description = "권한이 없는 알림"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음")
    })
    @DeleteMapping("/{alert_id}")
    public ResponseEntity<DeleteAlertResponseDto> deleteAlert(
            @Parameter(description = "알림 ID", example = "12345")
            @PathVariable("alert_id") Long alertId,
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = authorizationHeader.replace("Bearer ", "");

        Long userId = jwtUtil.getUserId(token);

        DeleteAlertResponseDto response = alertService.deleteAlert(userId, alertId);

        return ResponseEntity.ok(response);
    }

}