package com.loyai.loyaiproject.service;

import com.loyai.loyaiproject.dto.response.DashboardResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface DashboardService {
    ResponseEntity<DashboardResponseDto> getDashboardData(String bearerToken);
}
