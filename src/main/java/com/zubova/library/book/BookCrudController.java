package com.zubova.library.book;

import com.zubova.library.book.dto.BookDto;
import com.zubova.library.book.dto.BookRequestDto;
import com.zubova.library.common.crud.CrudController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/books")
public class BookCrudController extends CrudController<BookRequestDto, BookDto> {

    public BookCrudController(BookService bookService) {
        super(bookService);
    }

}