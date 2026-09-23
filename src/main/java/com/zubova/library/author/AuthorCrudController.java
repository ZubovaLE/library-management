package com.zubova.library.author;

import com.zubova.library.author.dto.AuthorDto;
import com.zubova.library.author.dto.AuthorRequestDto;
import com.zubova.library.common.crud.CrudController;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/authors")
@Tag(
        name = "Authors",
        description = "Author management"
)
public class AuthorCrudController extends CrudController<AuthorRequestDto, AuthorDto> {

    public AuthorCrudController(AuthorService authorService) {
        super(authorService);
    }

}