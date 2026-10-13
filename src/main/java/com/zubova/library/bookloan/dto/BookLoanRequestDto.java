package com.zubova.library.bookloan.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record BookLoanRequestDto(

        @Schema(
                description = "Identifier of an existing book",
                example = "1"
        )
        @NotNull
        @Positive
        Long bookId,

        @Schema(
                description = "Identifier of an existing reader",
                example = "1"
        )
        @NotNull
        @Positive
        Long readerId,

        @NotNull
        Instant dueDate

) {
}