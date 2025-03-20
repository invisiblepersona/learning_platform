package com.learning_platform.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor // ✅ Fixes "no constructor found" issue
public class AuthResponse {
    private String token;
}
