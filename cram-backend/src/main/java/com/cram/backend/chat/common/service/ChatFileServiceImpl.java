package com.cram.backend.chat.common.service;

import com.cram.backend.chat.common.dto.ChatFileUploadConfig;
import com.cram.backend.chat.common.dto.ChatFileUploadResult;
import com.cram.backend.user.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 채팅 파일 업로드 공통 서비스 구현체
 * GroupChat과 MeetingRoom에서 공통으로 사용하는 파일 업로드 로직을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatFileServiceImpl implements ChatFileService {

    @Override
    public ChatFileUploadResult uploadFiles(List<MultipartFile> files, ChatFileUploadConfig config, UserEntity user) {
        log.debug("[ChatFileService] groupId:{} files:{}", config.getGroupId(), files);

        // 1. 파일 저장 경로 설정 및 생성
        String uploadDirectory = createUploadDirectory(config);
        
        // 2. 파일 업로드 처리
        List<String> fileUrls = processFileUploads(files, uploadDirectory, config);
        
        return ChatFileUploadResult.builder()
                .fileUrls(fileUrls)
                .build();
    }

    /**
     * 업로드 디렉토리 생성
     * @param config 업로드 설정
     * @return 생성된 디렉토리 경로
     */
    private String createUploadDirectory(ChatFileUploadConfig config) {
        String rootPath = Paths.get(System.getProperty("user.dir"), config.getUploadPath())
                .toAbsolutePath()
                .toString();

        File dir = new File(rootPath);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new RuntimeException("디렉토리 생성에 실패했습니다: " + rootPath);
        }
        
        return rootPath;
    }

    /**
     * 파일 업로드 처리
     * @param files 업로드할 파일 목록
     * @param uploadDirectory 업로드 디렉토리
     * @param config 업로드 설정
     * @return 업로드된 파일 URL 목록
     */
    private List<String> processFileUploads(List<MultipartFile> files, String uploadDirectory, ChatFileUploadConfig config) {
        List<String> fileUrls = new ArrayList<>();
        List<String> savedFileNames = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                // 파일 유효성 검증
                validateFile(file);
                
                // 파일 저장
                String storedFileName = generateStoredFileName(file.getOriginalFilename());
                File uploadFile = new File(uploadDirectory, storedFileName);
                file.transferTo(uploadFile);

                // 파일 URL 생성
                String fileUrl = config.generateFileUrl(storedFileName);
                fileUrls.add(fileUrl);
                savedFileNames.add(storedFileName);

            } catch (IOException e) {
                // 실패 시 이미 저장된 파일들 정리
                cleanupUploadedFiles(uploadDirectory, savedFileNames);
                throw new RuntimeException("파일 업로드에 실패했습니다.", e);
            }
        }

        return fileUrls;
    }

    /**
     * 파일 유효성 검증
     * @param file 검증할 파일
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty() || file.getOriginalFilename() == null) {
            throw new RuntimeException("유효하지 않은 파일입니다.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("이미지 파일만 업로드 가능합니다.");
        }
    }

    /**
     * 저장될 파일명 생성
     * @param originalFilename 원본 파일명
     * @return UUID가 포함된 고유 파일명
     */
    private String generateStoredFileName(String originalFilename) {
        return UUID.randomUUID() + "_" + originalFilename;
    }

    /**
     * 업로드 실패 시 파일 정리
     * @param uploadDirectory 업로드 디렉토리
     * @param savedFileNames 저장된 파일명 목록
     */
    private void cleanupUploadedFiles(String uploadDirectory, List<String> savedFileNames) {
        for (String fileName : savedFileNames) {
            new File(uploadDirectory, fileName).delete();
        }
    }

}