package com.zubova.library.book;

import com.zubova.library.author.AuthorService;
import com.zubova.library.author.dto.AuthorRequestDto;
import com.zubova.library.book.dto.BookRequestDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Testcontainers
class BookConstraintsPostgresTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @DynamicPropertySource
    private static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @Test
    void shouldRejectAvailableCopiesGreaterThanTotalCopies() {
        long authorId = authorService.create(new AuthorRequestDto("Ada", "Lovelace", null)).id();
        long bookId = bookService.create(new BookRequestDto("Clean Code", authorId, "9780132350884", 2008, 3)).id();

        assertThrows(DataIntegrityViolationException.class, () ->
                jdbcTemplate.update("UPDATE books SET available_copies = total_copies + 1 WHERE id = ?", bookId));

        Integer availableCopies =
                jdbcTemplate.queryForObject("SELECT available_copies FROM books WHERE id = ?", Integer.class, bookId);
        assertEquals(3, availableCopies);
    }

}