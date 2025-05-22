package com.learning_platform.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String token; // secure token from email
    private String newPassword;
}

