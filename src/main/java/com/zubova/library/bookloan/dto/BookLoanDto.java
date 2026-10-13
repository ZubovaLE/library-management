package com.zubova.library.bookloan.dto;

import com.zubova.library.book.dto.BookDto;
import com.zubova.library.bookloan.BookLoanStatus;
import com.zubova.library.reader.dto.ReaderDto;

import java.time.Instant;

public record BookLoanDto(
        Long id,
        BookDto book,
        ReaderDto reader,
        Instant loanDate,
        Instant dueDate,
        Instant returnDate,
        BookLoanStatus status
) {
}