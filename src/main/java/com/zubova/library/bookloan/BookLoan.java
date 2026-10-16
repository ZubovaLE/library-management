package com.zubova.library.bookloan;

import com.zubova.library.book.Book;
import com.zubova.library.common.persistence.BaseEntity;
import com.zubova.library.reader.Reader;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "book_loans")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BookLoan extends BaseEntity {

    @Version
    @Setter(AccessLevel.NONE)
    private Long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reader_id", nullable = false)
    private Reader reader;

    @Column(name = "loan_date", nullable = false)
    private Instant loanDate;

    @Column(name = "due_date", nullable = false)
    private Instant dueDate;

    @Column(name = "return_date")
    private Instant returnDate;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookLoanStatus status;

}