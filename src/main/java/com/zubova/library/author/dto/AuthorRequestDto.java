package com.zubova.library.author.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequestDto(
        @NotBlank String firstName,
        @NotBlank String lastName,
        String middleName
) {
}