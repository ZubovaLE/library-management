package com.zubova.library.bookloan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface BookLoanRepository extends JpaRepository<BookLoan, Long> {

    @EntityGraph(attributePaths = {"book", "book.author", "reader"})
    Page<BookLoan> findAllByReaderId(Long readerId, Pageable pageable);

    @EntityGraph(attributePaths = {"book", "book.author", "reader"})
    Page<BookLoan> findAllByDueDateBeforeAndStatus(Instant now, BookLoanStatus status, Pageable pageable);

}