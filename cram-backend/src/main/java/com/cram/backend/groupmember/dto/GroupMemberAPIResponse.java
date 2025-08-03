package com.cram.backend.groupmember.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class GroupMemberAPIResponse<T> {
    private String message;
    private T data;
}
