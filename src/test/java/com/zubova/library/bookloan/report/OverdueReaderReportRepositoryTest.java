package com.zubova.library.bookloan.report;

import com.zubova.library.author.AuthorRepository;
import com.zubova.library.author.AuthorService;
import com.zubova.library.author.dto.AuthorRequestDto;
import com.zubova.library.book.BookRepository;
import com.zubova.library.book.BookService;
import com.zubova.library.book.dto.BookRequestDto;
import com.zubova.library.bookloan.BookLoanRepository;
import com.zubova.library.bookloan.BookLoanService;
import com.zubova.library.bookloan.dto.BookLoanRequestDto;
import com.zubova.library.reader.ReaderRepository;
import com.zubova.library.reader.ReaderService;
import com.zubova.library.reader.dto.ReaderRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class OverdueReaderReportRepositoryTest {

    private static final Instant NOW = Instant.parse("2026-10-13T12:00:00Z");

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @MockitoBean
    private Clock clock;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ReaderRepository readerRepository;

    @Autowired
    private BookLoanRepository bookLoanRepository;

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private ReaderService readerService;

    @Autowired
    private BookLoanService bookLoanService;

    @Autowired
    private OverdueReaderReportRepository overdueReaderReportRepository;

    @DynamicPropertySource
    private static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @BeforeEach
    void setUp() {
        when(clock.instant()).thenReturn(NOW);

        bookLoanRepository.deleteAll();
        bookRepository.deleteAll();
        readerRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    void findOverdueReadersShouldGroupAndSortLoansByReader() {
        long authorId = createAuthor("Author firstname 1", "Author lastname 1");
        long bookIdOne = createBook("Title 1", authorId, "1234567890", 1999, 3);
        long bookIdTwo = createBook("Title 2", authorId, "0987654321111", 2002, 5);
        long readerIdOne = createReader("Reader firstName 1", "Reader lastName 1", "reader1@gmail.com", "+77019876543");
        long readerIdTwo = createReader("Reader firstName 2", "Reader lastName 2", "reader2@gmail.com", "+77019876544");

        createBookLoan(bookIdOne, readerIdOne, NOW.plus(3, ChronoUnit.DAYS));
        createBookLoan(bookIdOne, readerIdTwo, NOW.plus(2, ChronoUnit.DAYS));
        createBookLoan(bookIdTwo, readerIdTwo, NOW.plus(1, ChronoUnit.DAYS));

        List<OverdueReaderReportDto> overdueReaders =
                overdueReaderReportRepository.findOverdueReaders(NOW.plus(4, ChronoUnit.DAYS));

        assertEquals(2, overdueReaders.size());

        OverdueReaderReportDto firstRecord = overdueReaders.get(0);
        assertEquals(readerIdTwo, firstRecord.readerId());
        assertEquals("Reader firstName 2", firstRecord.firstName());
        assertEquals("Reader lastName 2", firstRecord.lastName());
        assertEquals(2, firstRecord.overdueCount());
        assertEquals(NOW.plus(1, ChronoUnit.DAYS), firstRecord.oldestDueDate());

        OverdueReaderReportDto secondRecord = overdueReaders.get(1);
        assertEquals(readerIdOne, secondRecord.readerId());
        assertEquals("Reader firstName 1", secondRecord.firstName());
        assertEquals("Reader lastName 1", secondRecord.lastName());
        assertEquals(1, secondRecord.overdueCount());
        assertEquals(NOW.plus(3, ChronoUnit.DAYS), secondRecord.oldestDueDate());
    }

    @Test
    void findOverdueReadersShouldReturnEmptyListWhenNoLoansAreOverdue() {
        long authorId = createAuthor("Author firstname 1", "Author lastname 1");
        long bookIdOne = createBook("Title 1", authorId, "1234567890", 1999, 3);
        long readerIdOne = createReader("Reader firstName 1", "Reader lastName 1", "reader1@gmail.com", "+77019876543");
        long readerIdTwo = createReader("Reader firstName 2", "Reader lastName 2", "reader2@gmail.com", "+77019876544");

        createBookLoan(bookIdOne, readerIdOne, NOW.plus(3, ChronoUnit.DAYS));
        createBookLoan(bookIdOne, readerIdTwo, NOW.plus(2, ChronoUnit.DAYS));

        List<OverdueReaderReportDto> overdueReaders = overdueReaderReportRepository.findOverdueReaders(NOW);

        assertTrue(overdueReaders.isEmpty());
    }

    @Test
    void findOverdueReadersShouldExcludeReturnedLoans() {
        long authorId = createAuthor("Author firstname 1", "Author lastname 1");
        long bookIdOne = createBook("Title 1", authorId, "1234567890", 1999, 3);
        long readerIdOne = createReader("Reader firstName 1", "Reader lastName 1", "reader1@gmail.com", "+77019876543");
        long readerIdTwo = createReader("Reader firstName 2", "Reader lastName 2", "reader2@gmail.com", "+77019876544");

        long firstLoanId = createBookLoan(bookIdOne, readerIdOne, NOW.plus(1, ChronoUnit.DAYS));
        long secondLoanId = createBookLoan(bookIdOne, readerIdTwo, NOW.plus(1, ChronoUnit.DAYS));
        when(clock.instant()).thenReturn(NOW.plus(2, ChronoUnit.DAYS));
        bookLoanService.returnBook(firstLoanId);
        bookLoanService.returnBook(secondLoanId);

        List<OverdueReaderReportDto> overdueReaders =
                overdueReaderReportRepository.findOverdueReaders(NOW.plus(4, ChronoUnit.DAYS));

        assertTrue(overdueReaders.isEmpty());
    }

    private long createAuthor(String firstName, String lastName) {
        return authorService.create(new AuthorRequestDto(firstName, lastName, null)).id();
    }

    private long createBook(String title, Long authorId, String isbn, int publicationYear, Integer totalCopies) {
        return bookService.create(new BookRequestDto(title, authorId, isbn, publicationYear, totalCopies)).id();
    }

    private long createReader(String firstName, String lastName, String email, String phone) {
        return readerService.create(new ReaderRequestDto(firstName, lastName, email, phone)).id();
    }

    private long createBookLoan(Long bookId, Long readerId, Instant dueDate) {
        return bookLoanService.borrowBook(new BookLoanRequestDto(bookId, readerId, dueDate)).id();
    }

}