package com.zubova.library.book.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

public record BookRequestDto(

        @Schema(
                description = "Book title",
                example = "Clean Code"
        )
        @NotBlank
        @Size(max = 255)
        String title,

        @Schema(
                description = "Identifier of an existing author",
                example = "1"
        )
        @NotNull
        @Positive
        Long authorId,

        @Schema(
                description = "ISBN-10 or ISBN-13 without separators",
                example = "9780132350884"
        )
        @NotBlank
        @Pattern(
                regexp = "^(?:\\d{10}|\\d{13})$",
                message = "ISBN must contain exactly 10 or 13 digits"
        )
        String isbn,

        @Schema(
                description = "Publication year",
                example = "2008"
        )
        @NotNull
        Integer publicationYear,

        @Schema(
                description = "Total number of copies owned by the library",
                example = "5"
        )
        @NotNull
        @Min(0)
        Integer totalCopies

) {
}