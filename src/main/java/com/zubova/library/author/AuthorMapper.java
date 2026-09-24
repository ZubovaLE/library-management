package com.zubova.library.author;

import com.zubova.library.author.dto.AuthorDto;
import com.zubova.library.author.dto.AuthorRequestDto;
import com.zubova.library.common.mapping.BaseDtoMapper;
import com.zubova.library.common.mapping.CentralMapperConfig;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface AuthorMapper extends BaseDtoMapper<Author, AuthorDto> {

    Author toEntity(AuthorRequestDto request);

    void updateEntity(AuthorRequestDto request, @MappingTarget Author author);

}