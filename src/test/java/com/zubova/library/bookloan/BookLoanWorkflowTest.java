package com.zubova.library.bookloan;

import com.zubova.library.author.AuthorRepository;
import com.zubova.library.author.AuthorService;
import com.zubova.library.author.dto.AuthorRequestDto;
import com.zubova.library.book.BookRepository;
import com.zubova.library.book.BookService;
import com.zubova.library.book.dto.BookRequestDto;
import com.zubova.library.bookloan.dto.BookLoanDto;
import com.zubova.library.bookloan.dto.BookLoanRequestDto;
import com.zubova.library.exception.InvalidLoanRequestException;
import com.zubova.library.exception.ResourceConflictException;
import com.zubova.library.reader.ReaderRepository;
import com.zubova.library.reader.ReaderService;
import com.zubova.library.reader.dto.ReaderRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("dev")
class BookLoanWorkflowTest {

    private static final Instant NOW = Instant.parse("2026-10-13T12:00:00Z");
    @MockitoBean
    private Clock clock;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private BookService bookService;
    @Autowired
    private ReaderService readerService;
    @Autowired
    private BookLoanService loanService;
    @Autowired
    private BookLoanRepository loanRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ReaderRepository readerRepository;
    @Autowired
    private AuthorRepository authorRepository;

    @BeforeEach
    void cleanDatabase() {
        when(clock.instant()).thenReturn(NOW);

        loanRepository.deleteAll();
        bookRepository.deleteAll();
        readerRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    void issuingLastCopyCreatesOneLoanAndRejectsTheNext() {
        long bookId = createBook(1);
        long readerId = createReader();
        var request = new BookLoanRequestDto(bookId, readerId, NOW.plus(7, ChronoUnit.DAYS));

        BookLoanDto firstLoan = loanService.borrowBook(request);

        assertEquals(BookLoanStatus.ACTIVE, firstLoan.status());
        assertEquals(NOW, firstLoan.loanDate());
        assertNull(firstLoan.returnDate());
        assertEquals(request.dueDate(), firstLoan.dueDate());
        assertEquals(0, bookRepository.findById(bookId).orElseThrow().getAvailableCopies());
        assertEquals(1, loanRepository.count());

        assertThrows(ResourceConflictException.class, () -> loanService.borrowBook(request));
        assertEquals(1, loanRepository.count());
    }

    @Test
    void returningCopyRestoresStockExactlyOnce() {
        long bookId = createBook(1);
        long readerId = createReader();
        long loanId = loanService.borrowBook(new BookLoanRequestDto(
                bookId, readerId, NOW.plus(5, ChronoUnit.DAYS))).id();

        BookLoanDto returned = loanService.returnBook(loanId);
        assertEquals(BookLoanStatus.RETURNED, returned.status());
        assertEquals(NOW, returned.returnDate());
        assertEquals(1, bookRepository.findById(bookId).orElseThrow().getAvailableCopies());
        assertThrows(ResourceConflictException.class, () -> loanService.returnBook(loanId));
        assertEquals(1, bookRepository.findById(bookId).orElseThrow().getAvailableCopies());
        assertEquals(1, loanRepository.count());
    }

    @Test
    void dueDateMustBeAfterTheBorrowingTime() {
        long bookId = createBook(1);
        long readerId = createReader();
        assertThrows(InvalidLoanRequestException.class,
                () -> loanService.borrowBook(new BookLoanRequestDto(bookId, readerId, NOW)));
        assertEquals(0, loanRepository.count());
        assertEquals(1, bookRepository.findById(bookId).orElseThrow().getAvailableCopies());
    }

    @Test
    void readerHistoryAndOverdueResultsArePaged() {
        long bookId = createBook(1);
        long readerId = createReader();
        loanService.borrowBook(new BookLoanRequestDto(
                bookId, readerId, NOW.plus(3, ChronoUnit.DAYS)));

        var page = loanService.getReaderLoanHistory(readerId, PageRequest.of(0, 10));
        assertEquals(1, page.getTotalElements());
        assertEquals(0, loanService.getOverdueLoans(PageRequest.of(0, 10)).getTotalElements());

        when(clock.instant())
                .thenReturn(NOW.plus(4, ChronoUnit.DAYS));
        assertEquals(1, loanService.getOverdueLoans(PageRequest.of(0, 10)).getTotalElements());
        long loanId = page.getContent().getFirst().id();
        loanService.returnBook(loanId);
        assertEquals(0, loanService.getOverdueLoans(PageRequest.of(0, 10)).getTotalElements());
    }

    private long createBook(int totalCopies) {
        long authorId = authorService.create(
                new AuthorRequestDto("Ada", "Lovelace", null)).id();
        return bookService.create(
                new BookRequestDto("Concurrency in Practice", authorId,
                        "9780132350884", 2020, totalCopies)).id();
    }

    private long createReader() {
        var reader = readerService.create(
                new ReaderRequestDto("Alan", "Turing", "alan@example.com", "+79161234567"));
        assertEquals(NOW, reader.registrationDate());
        return reader.id();
    }

}