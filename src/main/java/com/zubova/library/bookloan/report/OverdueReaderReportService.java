package com.zubova.library.bookloan.report;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OverdueReaderReportService {

    private final OverdueReaderReportRepository repository;
    private final Clock clock;

    public List<OverdueReaderReportDto> getOverdueReaders() {
        return repository.findOverdueReaders(clock.instant());
    }

}