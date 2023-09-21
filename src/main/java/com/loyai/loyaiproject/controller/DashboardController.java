package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.request.DashboardRequestDto;
import com.loyai.loyaiproject.dto.response.DashboardResponseDto;
import com.loyai.loyaiproject.service.DashboardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pregame/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDto> getDashboardData(@RequestParam("bearer-token") String bearerToken){

        return dashboardService.getDashboardData(bearerToken);
    }
}
