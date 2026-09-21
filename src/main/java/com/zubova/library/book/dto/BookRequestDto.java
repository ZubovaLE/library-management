package com.zubova.library.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record BookRequestDto(
        @NotBlank String title,
        @NotNull Long authorId,
        @NotBlank
        @Pattern(
                regexp = "^(?:\\d{10}|\\d{13})$",
                message = "ISBN must contain exactly 10 or 13 digits"
        )
        String isbn,
        @NotNull Integer publicationYear,
        @NotNull @Min(0) Integer totalCopies
) {
}