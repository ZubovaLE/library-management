package com.zubova.library.book;

import com.zubova.library.author.Author;
import com.zubova.library.author.AuthorMapper;
import com.zubova.library.book.dto.BookDto;
import com.zubova.library.book.dto.BookRequestDto;
import com.zubova.library.common.mapping.BaseDtoMapper;
import com.zubova.library.common.mapping.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(
        config = CentralMapperConfig.class,
        uses = AuthorMapper.class
)
public interface BookMapper extends BaseDtoMapper<Book, BookDto> {

    @Mapping(target = "availableCopies", ignore = true)
    Book toEntity(BookRequestDto requestDto, Author author);

    @Mapping(target = "availableCopies", ignore = true)
    void updateEntity(BookRequestDto requestDto, Author author, @MappingTarget Book book);

}