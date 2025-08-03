package com.cram.backend.chat.groupchat.service;

import com.cram.backend.chat.groupchat.dto.response.GroupChatFileUploadResponseDto;
import com.cram.backend.user.entity.UserEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GroupChatFileService {
    GroupChatFileUploadResponseDto uploadFiles(List<MultipartFile> files, Long groupId, String origin, UserEntity user);
}