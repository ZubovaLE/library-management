package com.zubova.library.bookloan;

import com.zubova.library.common.crud.AbstractReadWriteService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class BookLoanPersistenceService extends AbstractReadWriteService<BookLoan, BookLoanRepository> {

    public BookLoanPersistenceService(BookLoanRepository repository) {
        super(repository);
    }

    public Page<BookLoan> findByReaderId(Long readerId, Pageable pageable) {
        return repository.findAllByReaderId(readerId, pageable);
    }

    public Page<BookLoan> findOverdueLoans(Instant now, Pageable pageable) {
        return repository.findAllByDueDateBeforeAndStatus(now, BookLoanStatus.ACTIVE, pageable);
    }

    @Override
    protected Class<BookLoan> getEntityClass() {
        return BookLoan.class;
    }

}