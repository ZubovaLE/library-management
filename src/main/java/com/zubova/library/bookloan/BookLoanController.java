package com.zubova.library.bookloan;

import com.zubova.library.bookloan.dto.BookLoanDto;
import com.zubova.library.bookloan.dto.BookLoanRequestDto;
import com.zubova.library.common.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class BookLoanController {

    private final BookLoanService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookLoanDto borrowBook(@Valid @RequestBody BookLoanRequestDto request) {
        return service.borrowBook(request);
    }

    @PutMapping("/{id}/return")
    public BookLoanDto returnBook(@PathVariable Long id) {
        return service.returnBook(id);
    }

    @GetMapping("/reader/{readerId}")
    public PageResponse<BookLoanDto> getReaderLoanHistory(@PathVariable Long readerId,
                                                      @PageableDefault(
                                                              size = 20,
                                                              sort = "loanDate",
                                                              direction = Sort.Direction.DESC
                                                      ) Pageable pageable) {
        return PageResponse.from(service.getReaderLoanHistory(readerId, pageable));
    }

    @GetMapping("/overdue")
    public PageResponse<BookLoanDto> getOverdueLoans(@PageableDefault(
            size = 20,
            sort = "dueDate",
            direction = Sort.Direction.ASC
    ) Pageable pageable) {
        return PageResponse.from(service.getOverdueLoans(pageable));
    }

}