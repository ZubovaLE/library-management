package com.zubova.library.author;

import com.zubova.library.author.dto.AuthorDto;
import com.zubova.library.author.dto.AuthorRequestDto;
import com.zubova.library.common.application.AbstractCrudApplicationService;
import org.springframework.stereotype.Service;

@Service
public class AuthorService extends AbstractCrudApplicationService<Author, AuthorRequestDto, AuthorDto> {

    private final AuthorMapper authorMapper;

    public AuthorService(AuthorCrudService crudService, AuthorMapper authorMapper) {
        super(crudService, authorMapper);
        this.authorMapper = authorMapper;
    }

    @Override
    protected Author createEntity(AuthorRequestDto request) {
        return authorMapper.toEntity(request);
    }

    @Override
    protected void updateEntity(AuthorRequestDto request, Author author) {
        authorMapper.updateEntity(request, author);
    }

}