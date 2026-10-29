
package com.zubova.library.statistics;

import com.zubova.library.book.BookRepository;
import com.zubova.library.bookloan.BookLoanRepository;
import com.zubova.library.bookloan.BookLoanStatus;
import com.zubova.library.reader.ReaderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LibraryStatisticsService {

    private final BookRepository bookRepository;
    private final ReaderRepository readerRepository;
    private final BookLoanRepository bookLoanRepository;

    @Transactional(readOnly = true)
    public LibraryStatistics getStatistics() {
        return new LibraryStatistics(
                bookRepository.count(),
                bookRepository.countAllCopies(),
                readerRepository.count(),
                bookLoanRepository.countByStatus(BookLoanStatus.ACTIVE)
        );
    }

}