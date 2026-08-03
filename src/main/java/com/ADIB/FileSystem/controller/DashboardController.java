package com.ADIB.FileSystem.controller;


import com.ADIB.FileSystem.Business.dto.response.DashboardStatisticsResponse;
import com.ADIB.FileSystem.Business.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@RestController
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping("/statistics")
    public DashboardStatisticsResponse getStatistics() {
        return dashboardService.getStatistics();
    }
}
