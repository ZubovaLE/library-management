package com.zubova.library.bookloan;

import com.zubova.library.book.Book;
import com.zubova.library.book.BookCrudService;
import com.zubova.library.bookloan.dto.BookLoanDto;
import com.zubova.library.bookloan.dto.BookLoanRequestDto;
import com.zubova.library.exception.InvalidLoanRequestException;
import com.zubova.library.exception.ResourceConflictException;
import com.zubova.library.reader.Reader;
import com.zubova.library.reader.ReaderCrudService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class BookLoanService {

    private final BookCrudService bookCrudService;
    private final ReaderCrudService readerCrudService;
    private final BookLoanPersistenceService bookLoanPersistenceService;
    private final BookLoanMapper bookLoanMapper;
    private final Clock clock;

    @Transactional
    public BookLoanDto borrowBook(BookLoanRequestDto request) {
        Instant now = clock.instant();
        if (request.dueDate() == null || !request.dueDate().isAfter(now)) {
            throw new InvalidLoanRequestException("Due date must be in the future");
        }

        Book book = bookCrudService.getById(request.bookId());
        Reader reader = readerCrudService.getById(request.readerId());
        book.borrowCopy();

        BookLoan loan = bookLoanMapper.toEntity(request, book, reader);
        loan.setLoanDate(now);
        loan.setStatus(BookLoanStatus.ACTIVE);
        BookLoan saved = bookLoanPersistenceService.save(loan);

        return bookLoanMapper.toDto(saved);
    }

    @Transactional
    public BookLoanDto returnBook(Long loanId) {
        BookLoan loan = bookLoanPersistenceService.getById(loanId);
        if (loan.getStatus() != BookLoanStatus.ACTIVE || loan.getReturnDate() != null) {
            throw new ResourceConflictException("Book loan has already been returned");
        }

        loan.getBook().returnCopy();
        loan.setReturnDate(clock.instant());
        loan.setStatus(BookLoanStatus.RETURNED);

        return bookLoanMapper.toDto(loan);
    }

    @Transactional(readOnly = true)
    public Page<BookLoanDto> getReaderLoanHistory(Long readerId, Pageable pageable) {
        return bookLoanPersistenceService.findByReaderId(readerId, pageable).map(bookLoanMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Page<BookLoanDto> getOverdueLoans(Pageable pageable) {
        return bookLoanPersistenceService.findOverdueLoans(clock.instant(), pageable).map(bookLoanMapper::toDto);
    }

}