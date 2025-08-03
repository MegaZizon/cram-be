package com.cram.backend.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Long userId;
    private String role;
    private String name;
    private String username;
    private String email;
    private String profileImage;
    private LocalDate birthDate;
    private String phone;

}
