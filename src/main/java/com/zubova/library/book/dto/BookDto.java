package com.zubova.library.book.dto;

import com.zubova.library.author.dto.AuthorDto;

public record BookDto(
        Long id,
        String title,
        AuthorDto author,
        String isbn,
        int publicationYear,
        int totalCopies,
        int availableCopies
) {
}