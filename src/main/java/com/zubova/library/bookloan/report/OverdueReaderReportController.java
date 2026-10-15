package com.zubova.library.bookloan.report;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OverdueReaderReportController {

    private final OverdueReaderReportService reportService;

    @GetMapping("/api/reports/overdue-readers")
    public List<OverdueReaderReportDto> getOverdueReaders() {
        return reportService.getOverdueReaders();
    }

}