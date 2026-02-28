package com.bankapp.controller;

import com.bankapp.dto.dashboard.DashboardResponse;
import com.bankapp.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_BASIC')")
    ResponseEntity<DashboardResponse> getDashboard(){
        DashboardResponse dashboardResponse = dashboardService.getDashboard();
        return ResponseEntity.ok(dashboardResponse);
    }
}
