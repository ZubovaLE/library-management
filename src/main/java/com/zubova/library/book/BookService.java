package com.zubova.library.book;

import com.zubova.library.author.Author;
import com.zubova.library.author.AuthorCrudService;
import com.zubova.library.book.dto.BookDto;
import com.zubova.library.book.dto.BookRequestDto;
import com.zubova.library.common.application.AbstractCrudApplicationService;
import com.zubova.library.exception.ResourceConflictException;
import org.springframework.stereotype.Service;

@Service
public class BookService extends AbstractCrudApplicationService<Book, BookRequestDto, BookDto> {

    private final BookCrudService bookCrudService;
    private final BookMapper bookMapper;
    private final AuthorCrudService authorCrudService;

    public BookService(BookCrudService bookCrudService, BookMapper bookMapper, AuthorCrudService authorCrudService) {
        super(bookCrudService, bookMapper);
        this.bookCrudService = bookCrudService;
        this.bookMapper = bookMapper;
        this.authorCrudService = authorCrudService;
    }

    @Override
    protected Book createEntity(BookRequestDto request) {
        validateIsbnAvailableForCreate(request.isbn());

        Author author = authorCrudService.getById(request.authorId());

        Book book = bookMapper.toEntity(request, author);
        book.setAvailableCopies(book.getTotalCopies());

        return book;
    }

    @Override
    protected void updateEntity(BookRequestDto request, Book book) {
        validateIsbnAvailableForUpdate(request.isbn(), book.getId());

        Author author = authorCrudService.getById(request.authorId());

        int borrowedCopies = book.getTotalCopies() - book.getAvailableCopies();

        validateTotalCopies(request.totalCopies(), borrowedCopies);

        bookMapper.updateEntity(request, author, book);

        book.setAvailableCopies(book.getTotalCopies() - borrowedCopies);
    }

    private void validateIsbnAvailableForCreate(String isbn) {
        if (bookCrudService.existsByIsbn(isbn)) {
            throw new ResourceConflictException("Book with ISBN %s already exists".formatted(isbn));
        }
    }

    private void validateIsbnAvailableForUpdate(String isbn, Long bookId) {
        if (bookCrudService.existsByIsbnAndIdNot(isbn, bookId)) {
            throw new ResourceConflictException("Book with ISBN %s already exists".formatted(isbn));
        }
    }

    private void validateTotalCopies(int totalCopies, int borrowedCopies) {
        if (totalCopies < borrowedCopies) {
            throw new ResourceConflictException("Total copies cannot be less than borrowed copies");
        }
    }

}