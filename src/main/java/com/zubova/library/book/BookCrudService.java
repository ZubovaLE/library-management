package com.zubova.library.book;

import com.zubova.library.common.crud.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class BookCrudService extends AbstractCrudService<Book> {

    public BookCrudService(BookRepository repository) {
        super(repository);
    }

}