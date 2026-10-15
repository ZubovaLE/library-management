package com.zubova.library.bookloan.report;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OverdueReaderReportRepository {

    private static final String QUERY = "SELECT " +
            "   r.id AS reader_id,\n" +
            "   r.first_name,\n" +
            "   r.last_name,\n" +
            "   COUNT(*) AS overdue_count,\n" +
            "   MIN(l.due_date) AS oldest_due_date\n" +
            "  FROM book_loans l " +
            " JOIN readers r " +
            " ON l.reader_id = r.id " +
            " WHERE l.status = 'ACTIVE' " +
            " AND l.due_date < ? " +
            " GROUP BY r.id, r.first_name, r.last_name " +
            " ORDER BY overdue_count DESC, oldest_due_date ASC, reader_id ASC;";
    private static final RowMapper<OverdueReaderReportDto> ROW_MAPPER = (ResultSet rs, int rowNum) ->
            new OverdueReaderReportDto(rs.getLong("reader_id"), rs.getString("first_name"),
                    rs.getString("last_name"), rs.getLong("overdue_count"),
                    rs.getTimestamp("oldest_due_date").toInstant());
    private final JdbcTemplate jdbcTemplate;

    List<OverdueReaderReportDto> findOverdueReaders(Instant now) {
        return jdbcTemplate.query(QUERY, ROW_MAPPER, Timestamp.from(now));
    }

}