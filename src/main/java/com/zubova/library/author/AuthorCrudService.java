package com.zubova.library.author;

import com.zubova.library.common.crud.AbstractCrudService;
import org.springframework.stereotype.Service;

@Service
public class AuthorCrudService extends AbstractCrudService<Author> {

    public AuthorCrudService(AuthorRepository repository) {
        super(repository);
    }

}