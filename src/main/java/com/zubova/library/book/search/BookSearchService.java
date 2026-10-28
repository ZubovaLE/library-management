
package com.zubova.library.book.search;

import com.zubova.library.author.Author;
import com.zubova.library.book.Book;
import com.zubova.library.book.BookMapper;
import com.zubova.library.book.BookRepository;
import com.zubova.library.book.dto.BookDto;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class BookSearchService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;

    @Transactional(readOnly = true)
    public Page<BookDto> search(BookSearchFilter filter, Pageable pageable) {
        Specification<Book> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.isNotBlank(filter.title())) {
                predicates.add(cb.like(
                        cb.lower(root.get("title")),
                        "%" + filter.title().trim().toLowerCase(Locale.ROOT) + "%"
                ));
            }

            if (filter.authorId() != null) {
                predicates.add(cb.equal(root.get("author").get("id"), filter.authorId()));
            }

            if (StringUtils.isNotBlank(filter.author())) {
                Join<Book, Author> author = root.join("author");

                for (String word : filter.author().trim().split("\\s+")) {
                    String pattern = "%" + word.toLowerCase(Locale.ROOT) + "%";

                    predicates.add(cb.or(
                            cb.like(cb.lower(author.get("firstName")), pattern),
                            cb.like(cb.lower(author.get("lastName")), pattern),
                            cb.like(cb.lower(author.get("middleName")), pattern)
                    ));
                }
            }

            if (StringUtils.isNotBlank(filter.isbn())) {
                predicates.add(cb.equal(root.get("isbn"), filter.isbn().trim()));
            }

            if (filter.publicationYear() != null) {
                predicates.add(cb.equal(root.get("publicationYear"), filter.publicationYear()));
            }

            if (filter.available() != null) {
                predicates.add(filter.available()
                        ? cb.greaterThan(root.get("availableCopies"), 0)
                        : cb.equal(root.get("availableCopies"), 0)
                );
            }

            return cb.and(predicates.toArray(Predicate[]::new));
        };

        return bookRepository.findAll(specification, pageable).map(bookMapper::toDto);
    }

}