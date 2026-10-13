package com.zubova.library.reader;

import com.zubova.library.common.application.AbstractCrudApplicationService;
import com.zubova.library.reader.dto.ReaderDto;
import com.zubova.library.reader.dto.ReaderRequestDto;
import org.springframework.stereotype.Service;

import java.time.Clock;

@Service
public class ReaderService extends AbstractCrudApplicationService<Reader, ReaderRequestDto, ReaderDto> {

    private final ReaderMapper readerMapper;
    private final Clock clock;

    public ReaderService(ReaderCrudService readerCrudService, ReaderMapper readerMapper, Clock clock) {
        super(readerCrudService, readerMapper);
        this.readerMapper = readerMapper;
        this.clock = clock;
    }

    @Override
    protected Reader createEntity(ReaderRequestDto request) {
        Reader reader = readerMapper.toEntity(request);
        reader.setRegistrationDate(clock.instant());
        return reader;
    }

    @Override
    protected void updateEntity(ReaderRequestDto request, Reader entity) {
        readerMapper.updateEntity(request, entity);
    }

}