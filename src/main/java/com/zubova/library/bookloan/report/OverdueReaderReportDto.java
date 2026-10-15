package com.zubova.library.bookloan.report;

import java.time.Instant;

public record OverdueReaderReportDto(

        Long readerId,
        String firstName,
        String lastName,
        long overdueCount,
        Instant oldestDueDate

) {
}
