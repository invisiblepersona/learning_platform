
package com.learning_platform.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContactRequest {
    private String name;
    private String email;
    private String message;
}
