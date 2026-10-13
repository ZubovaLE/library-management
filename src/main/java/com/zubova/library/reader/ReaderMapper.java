package com.zubova.library.reader;

import com.zubova.library.common.mapping.BaseDtoMapper;
import com.zubova.library.common.mapping.CentralMapperConfig;
import com.zubova.library.reader.dto.ReaderDto;
import com.zubova.library.reader.dto.ReaderRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(config = CentralMapperConfig.class)
public interface ReaderMapper extends BaseDtoMapper<Reader, ReaderDto> {

    @Mapping(target = "registrationDate", ignore = true)
    Reader toEntity(ReaderRequestDto request);

    @Mapping(target = "registrationDate", ignore = true)
    void updateEntity(ReaderRequestDto request, @MappingTarget Reader reader);

}