package com.zubova.library.book;

import com.zubova.library.author.Author;
import com.zubova.library.author.AuthorCrudService;
import com.zubova.library.book.dto.BookDto;
import com.zubova.library.book.dto.BookRequestDto;
import com.zubova.library.common.application.AbstractCrudApplicationService;
import org.springframework.stereotype.Service;

@Service
public class BookService extends AbstractCrudApplicationService<Book, BookRequestDto, BookDto> {

    private final AuthorCrudService authorCrudService;
    private final BookMapper bookMapper;

    public BookService(BookCrudService crudService, BookMapper bookMapper, AuthorCrudService authorCrudService) {
        super(crudService, bookMapper);
        this.bookMapper = bookMapper;
        this.authorCrudService = authorCrudService;
    }

    @Override
    protected Book createEntity(BookRequestDto request) {
        Author author = authorCrudService.getById(request.authorId());

        Book book = bookMapper.toEntity(request, author);
        book.setAvailableCopies(book.getTotalCopies());

        return book;
    }

    @Override
    protected void updateEntity(BookRequestDto request, Book book) {
        Author author = authorCrudService.getById(request.authorId());

        int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();

        validateTotalCopies(request.totalCopies(), borrowedCopies);

        bookMapper.updateEntity(request, author, book);

        book.setAvailableCopies(book.getTotalCopies() - borrowedCopies);
    }

    private void validateTotalCopies(int totalCopies, int borrowedCopies) {
        if (totalCopies < borrowedCopies) {
            throw new IllegalArgumentException("Total copies cannot be less than borrowed copies");
        }
    }

}