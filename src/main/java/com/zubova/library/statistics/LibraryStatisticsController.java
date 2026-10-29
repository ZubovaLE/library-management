package com.zubova.library.statistics;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lbrary/statistics")
@RequiredArgsConstructor
public class LibraryStatisticsController {

    private final LibraryStatisticsService libraryStatisticsService;

    @GetMapping
    public LibraryStatistics getStatistics() {
        return libraryStatisticsService.getStatistics();
    }

}