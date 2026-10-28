
package com.zubova.library.book.search;

import com.zubova.library.author.AuthorRepository;
import com.zubova.library.author.AuthorService;
import com.zubova.library.author.dto.AuthorRequestDto;
import com.zubova.library.book.BookRepository;
import com.zubova.library.book.BookService;
import com.zubova.library.book.dto.BookDto;
import com.zubova.library.book.dto.BookRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Testcontainers
class BookSearchServiceTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private BookSearchService bookSearchService;

    private long markTwainId;
    private long tolstoyId;
    private long tomSawyerId;
    private long warAndPeaceId;
    private long annaKareninaId;

    @DynamicPropertySource
    private static void configureDatabase(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
        authorRepository.deleteAll();

        markTwainId = createAuthor("Mark", "Twain", null);
        tolstoyId = createAuthor("Лев", "Толстой", "Николаевич");

        tomSawyerId = createBook("Tom Sawyer", markTwainId, "1234567890123", 1973, 5);
        warAndPeaceId = createBook("Война и мир", tolstoyId, "0987654321098", 1976, 10);
        annaKareninaId = createBook("Анна Каренина", tolstoyId, "5555555555555", 1985, 0);
    }

    @ParameterizedTest
    @CsvSource(value = {"Лев Толс", "Лев", "лев", "Толстой", "лев толстой", "Толстой Лев",
            "Николаевич", "Лев Николаевич Толстой"})
    void searchByAuthor_shouldReturnBooks(String author) {
        BookSearchFilter filter = new BookSearchFilter(null, author, null, null, null, null);
        Page<BookDto> result = bookSearchService.search(filter, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        assertEquals(Set.of(warAndPeaceId, annaKareninaId), Set.copyOf(result.getContent().stream().map(BookDto::id).toList()));
    }

    @Test
    void searchByAuthorId_shouldReturnBooks() {
        BookSearchFilter filter = new BookSearchFilter(null, null, tolstoyId, null, null, null);
        Page<BookDto> result = bookSearchService.search(filter, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertEquals(Set.of(warAndPeaceId, annaKareninaId),
                Set.copyOf(result.getContent().stream().map(BookDto::id).toList()));
    }

    @Test
    void searchByTitle_shouldIgnoreCase() {
        BookSearchFilter filter = new BookSearchFilter("tOm", null, null, null, null, null);
        Page<BookDto> result = bookSearchService.search(filter, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(tomSawyerId, result.getContent().get(0).id());
    }

    @Test
    void searchByIsbn_shouldReturnBooks() {
        BookSearchFilter filter = new BookSearchFilter(null, null, null, "1234567890123", null, null);
        Page<BookDto> result = bookSearchService.search(filter, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(tomSawyerId, result.getContent().get(0).id());
    }

    @Test
    void searchByPublicationYear_shouldReturnBooks() {
        BookSearchFilter filter = new BookSearchFilter(null, null, null, null, 1976, null);
        Page<BookDto> result = bookSearchService.search(filter, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(warAndPeaceId, result.getContent().get(0).id());
    }

    @Test
    void searchByAvailability_shouldReturnBooks() {
        BookSearchFilter filter = new BookSearchFilter(null, null, null, null, null, true);
        Page<BookDto> result = bookSearchService.search(filter, PageRequest.of(0, 10));

        assertEquals(2, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getContent().size());
        assertEquals(Set.of(tomSawyerId, warAndPeaceId),
                Set.copyOf(result.getContent().stream().map(BookDto::id).toList()));
    }

    @Test
    void searchByAbsence_shouldReturnMatchingBooks() {
        BookSearchFilter filter = new BookSearchFilter(null, null, null, null, null, false);
        Page<BookDto> result = bookSearchService.search(filter, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(annaKareninaId, result.getContent().get(0).id());
    }

    @Test
    void searchByMultipleFilters_shouldReturnMatchingBooks() {
        createBook("Война и мир", markTwainId, "1111111111111", 1976, 5);
        createBook("Война и мир", tolstoyId, "2222222222222", 1980, 5);
        createBook("Война и мир", tolstoyId, "3333333333333", 1976, 0);
        createBook("Другая книга", tolstoyId, "4444444444444", 1976, 5);

        BookSearchFilter filter = new BookSearchFilter("Война ", "лев тол", null, null, 1976, true);
        Page<BookDto> result = bookSearchService.search(filter, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getTotalPages());
        assertEquals(1, result.getContent().size());
        assertEquals(warAndPeaceId, result.getContent().get(0).id());
    }

    @Test
    void searchWithPageable_shouldReturnSortedPages() {
        BookSearchFilter filter = new BookSearchFilter(null, null, null, null, null, null);

        Page<BookDto> firstPage = bookSearchService.search(filter,
                PageRequest.of(0, 2, Sort.by(Sort.Direction.DESC, "publicationYear")));

        Page<BookDto> secondPage = bookSearchService.search(filter,
                PageRequest.of(1, 2, Sort.by(Sort.Direction.DESC, "publicationYear")));

        assertEquals(3, firstPage.getTotalElements());
        assertEquals(2, firstPage.getTotalPages());
        assertEquals(List.of(annaKareninaId, warAndPeaceId),
                firstPage.getContent().stream().map(BookDto::id).toList());

        assertEquals(3, secondPage.getTotalElements());
        assertEquals(2, secondPage.getTotalPages());
        assertEquals(List.of(tomSawyerId),
                secondPage.getContent().stream().map(BookDto::id).toList());
    }

    @Test
    void searchWithoutMatches_shouldReturnEmptyPage() {
        BookSearchFilter filter = new BookSearchFilter("Несуществующая книга", null, null, null, null, null);
        Page<BookDto> result = bookSearchService.search(filter, PageRequest.of(0, 10));

        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    private long createAuthor(String firstName, String lastName, String middleName) {
        return authorService.create(new AuthorRequestDto(firstName, lastName, middleName)).id();
    }

    private long createBook(String title, Long authorId, String isbn, int publicationYear, Integer totalCopies) {
        return bookService.create(new BookRequestDto(title, authorId, isbn, publicationYear, totalCopies)).id();
    }

}