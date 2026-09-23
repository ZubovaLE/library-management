package com.zubova.library.book;

import com.zubova.library.common.crud.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class BookCrudService extends AbstractCrudService<Book, BookRepository> {

    public BookCrudService(BookRepository repository) {
        super(repository);
    }

    @Override
    protected Class<Book> getEntityClass() {
        return Book.class;
    }

    public boolean existsByIsbn(String isbn) {
        return repository.existsByIsbn(isbn);
    }

    public boolean existsByIsbnAndIdNot(String isbn, Long id) {
        return repository.existsByIsbnAndIdNot(isbn, id);
    }

}