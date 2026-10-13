package com.zubova.library.bookloan;

import com.zubova.library.book.Book;
import com.zubova.library.book.BookMapper;
import com.zubova.library.bookloan.dto.BookLoanDto;
import com.zubova.library.bookloan.dto.BookLoanRequestDto;
import com.zubova.library.common.mapping.BaseDtoMapper;
import com.zubova.library.common.mapping.CentralMapperConfig;
import com.zubova.library.reader.Reader;
import com.zubova.library.reader.ReaderMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(
        config = CentralMapperConfig.class,
        uses = {BookMapper.class, ReaderMapper.class}
)
public interface BookLoanMapper extends BaseDtoMapper<BookLoan, BookLoanDto> {

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "loanDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "returnDate", ignore = true)
    BookLoan toEntity(BookLoanRequestDto requestDto, Book book, Reader reader);

}