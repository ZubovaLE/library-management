package com.zubova.library.bookloan;

import com.zubova.library.author.AuthorRepository;
import com.zubova.library.author.AuthorService;
import com.zubova.library.author.dto.AuthorRequestDto;
import com.zubova.library.book.BookRepository;
import com.zubova.library.book.BookService;
import com.zubova.library.book.dto.BookRequestDto;
import com.zubova.library.bookloan.dto.BookLoanRequestDto;
import com.zubova.library.exception.ResourceConflictException;
import com.zubova.library.reader.ReaderRepository;
import com.zubova.library.reader.ReaderService;
import com.zubova.library.reader.dto.ReaderRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class BookLoanConcurrencyPostgresTest {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void database(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Autowired
    private PlatformTransactionManager transactionManager;
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
    void reset() {
        loanRepository.deleteAll();
        bookRepository.deleteAll();
        readerRepository.deleteAll();
        authorRepository.deleteAll();
    }

    @Test
    void versionCheckRejectsConcurrentWritesBasedOnTheSameSnapshot() throws Exception {
        long authorId = authorService.create(new AuthorRequestDto("Ada", "Lovelace", null)).id();
        long bookId = bookService.create(new BookRequestDto(
                "Concurrency in Practice", authorId, "9780132350884", 2020, 1)).id();

        CyclicBarrier bothRead = new CyclicBarrier(2);
        try (var pool = Executors.newFixedThreadPool(2)) {
            Callable<Boolean> update = () -> {
                try {
                    new TransactionTemplate(transactionManager).execute(status -> {
                        var book = bookRepository.findById(bookId).orElseThrow();
                        try {
                            bothRead.await(10, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            throw new IllegalStateException("Could not synchronize transactions", e);
                        }
                        book.borrowCopy();
                        return null;
                    });
                    return true;
                } catch (OptimisticLockingFailureException conflict) {
                    return false;
                }
            };
            var first = pool.submit(update);
            var second = pool.submit(update);
            long successes = (first.get(30, TimeUnit.SECONDS) ? 1 : 0)
                    + (second.get(30, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, successes);
            assertEquals(0, bookRepository.findById(bookId).orElseThrow().getAvailableCopies());
        }
    }

    @Test
    void simultaneousReturnsOfTheSameLoanRestoreOnlyOneCopy() throws Exception {
        long authorId = authorService.create(new AuthorRequestDto("Ada", "Lovelace", null)).id();
        long bookId = bookService.create(new BookRequestDto(
                "Concurrency in Practice", authorId, "9780132350884", 2020, 3)).id();
        long readerId = readerService.create(new ReaderRequestDto(
                "Alice", "Example", "alice@example.com", "+79161234567")).id();
        long loanId = loanService.borrowBook(new BookLoanRequestDto(
                bookId, readerId, Instant.now().plus(7, ChronoUnit.DAYS))).id();
        long anotherReaderId = readerService.create(new ReaderRequestDto(
                "Bob", "Example", "bob@example.com", "+79161234568")).id();
        loanService.borrowBook(new BookLoanRequestDto(
                bookId, anotherReaderId, Instant.now().plus(7, ChronoUnit.DAYS)));

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var pool = Executors.newFixedThreadPool(2)) {
            java.util.concurrent.Callable<Boolean> returnOnce = () -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) {
                    throw new AssertionError("workers not started");
                }
                try {
                    loanService.returnBook(loanId);
                    return true;
                } catch (ResourceConflictException | OptimisticLockingFailureException expected) {
                    return false;
                }
            };
            var a = pool.submit(returnOnce);
            var b = pool.submit(returnOnce);
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();
            long successes = (a.get(30, TimeUnit.SECONDS) ? 1 : 0)
                    + (b.get(30, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, successes);
        }
        assertEquals(2, bookRepository.findById(bookId).orElseThrow().getAvailableCopies());
        assertEquals(BookLoanStatus.RETURNED, loanRepository.findById(loanId).orElseThrow().getStatus());
    }

    @Test
    void lastCopyMayBeBorrowedOnlyOnceEvenWhenTwoRequestsRace() throws Exception {
        long authorId = authorService.create(new AuthorRequestDto("Ada", "Lovelace", null)).id();
        long bookId = bookService.create(new BookRequestDto(
                "Concurrency in Practice", authorId, "9780132350884", 2020, 1)).id();
        long readerA = readerService.create(new ReaderRequestDto(
                "Alice", "Example", "alice@example.com", "+79161234567")).id();
        long readerB = readerService.create(new ReaderRequestDto(
                "Bob", "Example", "bob@example.com", "+79161234568")).id();
        Instant dueDate = Instant.now().plus(7, ChronoUnit.DAYS);

        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        try (var pool = Executors.newFixedThreadPool(2)) {
            var a = pool.submit(() -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) {
                    throw new AssertionError("workers not started");
                }
                return borrowOnce(bookId, readerA, dueDate);
            });
            var b = pool.submit(() -> {
                ready.countDown();
                if (!start.await(10, TimeUnit.SECONDS)) {
                    throw new AssertionError("workers not started");
                }
                return borrowOnce(bookId, readerB, dueDate);
            });
            assertTrue(ready.await(10, TimeUnit.SECONDS));
            start.countDown();
            long successes = (a.get(30, TimeUnit.SECONDS) ? 1 : 0)
                    + (b.get(30, TimeUnit.SECONDS) ? 1 : 0);
            assertEquals(1, successes);
        }
        assertEquals(1, loanRepository.count());
        assertEquals(0, bookRepository.findById(bookId).orElseThrow().getAvailableCopies());
    }

    private boolean borrowOnce(Long bookId, Long readerId, Instant dueDate) {
        try {
            loanService.borrowBook(new BookLoanRequestDto(bookId, readerId, dueDate));
            return true;
        } catch (ResourceConflictException | OptimisticLockingFailureException expectedConflict) {
            return false;
        }
    }

}