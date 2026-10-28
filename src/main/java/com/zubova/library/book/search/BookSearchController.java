package com.zubova.library.book.search;

import com.zubova.library.book.dto.BookDto;
import com.zubova.library.common.response.PageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books/search")
@RequiredArgsConstructor
public class BookSearchController {

    private final BookSearchService bookSearchService;

    @GetMapping
    public PageResponse<BookDto> searchBooks(@Valid @ModelAttribute BookSearchFilter filter,
                                             @PageableDefault(
                                                     size = 20,
                                                     sort = "id",
                                                     direction = Sort.Direction.ASC
                                             ) Pageable pageable) {
        return PageResponse.from(bookSearchService.search(filter, pageable));
    }

}