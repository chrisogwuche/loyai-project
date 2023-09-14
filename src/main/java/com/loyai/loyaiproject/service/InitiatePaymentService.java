package com.loyai.loyaiproject.service;

import com.loyai.loyaiproject.dto.request.PayNowRequestDto;
import com.loyai.loyaiproject.dto.response.PayNowResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface InitiatePaymentService {
    ResponseEntity<PayNowResponseDto> getToken(PayNowRequestDto tokenRequestDto, HttpServletRequest servletRequest);
}
