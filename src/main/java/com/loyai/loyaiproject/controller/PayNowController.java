package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.request.PayNowRequestDto;
import com.loyai.loyaiproject.dto.response.PayNowResponseDto;
import com.loyai.loyaiproject.service.PayNowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments/initiate")
@CrossOrigin("http://localhost:5173/")
public class PayNowController {
    private final PayNowService payNowService;

    @PostMapping()
    public ResponseEntity<PayNowResponseDto> initiatePayment(@RequestBody @Valid PayNowRequestDto payNowRequestDto) {

        return payNowService.getToken(payNowRequestDto);
    }

}
