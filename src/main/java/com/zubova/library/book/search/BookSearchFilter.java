package com.zubova.library.book.search;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

public record BookSearchFilter(
        String title,
        String author,
        @Positive Long authorId,
        String isbn,
        @Min(1450) Integer publicationYear,
        Boolean available
) {
}