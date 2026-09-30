package com.example.testbanking.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "{auth.email.required}")
    @Email(message = "{auth.email.invalid}")
    private String email;

    @NotBlank(message = "{auth.password.required}")
    @Size(min = 6, message = "{auth.password.size}")
    private String password;

    @NotBlank(message = "{auth.firstname.required}")
    private String firstName;

    @NotBlank(message = "{auth.lastname.required}")
    private String lastName;
}
