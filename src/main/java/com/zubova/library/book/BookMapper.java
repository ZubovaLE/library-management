package com.zubova.library.book;

import com.zubova.library.author.Author;
import com.zubova.library.author.AuthorMapper;
import com.zubova.library.book.dto.BookDto;
import com.zubova.library.book.dto.BookRequestDto;
import com.zubova.library.common.mapping.BaseDtoMapper;
import org.mapstruct.*;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        uses = AuthorMapper.class,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface BookMapper extends BaseDtoMapper<Book, BookDto> {

    @Mapping(target = "availableCopies", ignore = true)
    Book toEntity(BookRequestDto requestDto, Author author);

    @Mapping(target = "availableCopies", ignore = true)
    void updateEntity(BookRequestDto requestDto, Author author, @MappingTarget Book book);

}