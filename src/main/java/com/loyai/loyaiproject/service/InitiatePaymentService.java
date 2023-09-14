package com.loyai.loyaiproject.service;

import com.loyai.loyaiproject.dto.request.PaymentRequestDto;
import com.loyai.loyaiproject.dto.response.PayNowResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface InitiatePaymentService {
    ResponseEntity<PayNowResponseDto> getToken(PaymentRequestDto tokenRequestDto, HttpServletRequest servletRequest);
}
