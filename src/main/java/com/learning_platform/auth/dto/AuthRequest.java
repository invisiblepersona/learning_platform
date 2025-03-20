package com.learning_platform.auth.dto;

import lombok.Getter;
import lombok.Setter;
import com.learning_platform.auth.models.Role;

@Getter
@Setter
public class AuthRequest {
    private String email;
    private String password;
    private Role role; // ✅ Allow users to specify their role (STUDENT or ADMIN)
}
