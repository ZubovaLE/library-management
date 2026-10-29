package com.zubova.library.statistics;

public record LibraryStatistics(

        Long totalBookEntries,
        Long totalBookCopies,
        Long totalReadersCount,
        Long totalActiveLoansCount

) {
}