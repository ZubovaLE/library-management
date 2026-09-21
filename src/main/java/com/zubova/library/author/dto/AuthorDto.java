package com.zubova.library.author.dto;

public record AuthorDto(
        Long id,
        String firstName,
        String lastName,
        String middleName
) {
}