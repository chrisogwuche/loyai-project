package com.loyai.loyaiproject.service;

import com.loyai.loyaiproject.dto.request.PayNowRequestDto;
import com.loyai.loyaiproject.dto.response.PayNowResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface PayNowService {
    ResponseEntity<PayNowResponseDto> getToken(PayNowRequestDto payNowRequestDto);
}
