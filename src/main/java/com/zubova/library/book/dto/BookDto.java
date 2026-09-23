package com.zubova.library.book.dto;

import com.zubova.library.author.dto.AuthorDto;
import io.swagger.v3.oas.annotations.media.Schema;

public record BookDto(

        Long id,
        String title,
        AuthorDto author,
        String isbn,
        int publicationYear,
        int totalCopies,
        @Schema(
                description = "Number of copies currently available for loan",
                example = "3"
        )
        int availableCopies

) {
}