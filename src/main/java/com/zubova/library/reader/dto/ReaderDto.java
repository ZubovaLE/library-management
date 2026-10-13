package com.zubova.library.reader.dto;

import java.time.Instant;

public record ReaderDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        Instant registrationDate
) {
}