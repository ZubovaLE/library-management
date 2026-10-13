package com.zubova.library.reader.dto;

import jakarta.validation.constraints.*;

import java.time.Instant;

public record ReaderRequestDto(

        @NotBlank
        @Size(max = 100)
        String firstName,

        @NotBlank
        @Size(max = 100)
        String lastName,

        @NotBlank
        @Email
        @Size(max = 255)
        String email,

        @NotBlank
        @Pattern(regexp = "\\+[1-9]\\d{9,14}", message = "phone must use E.164 format, e.g. +79161234567")
        String phone

) {
}