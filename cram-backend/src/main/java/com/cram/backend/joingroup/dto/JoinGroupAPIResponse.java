package com.cram.backend.joingroup.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JoinGroupAPIResponse<T> {
    private String message;
    private T data;
}
