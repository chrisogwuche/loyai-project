package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.request.PayNowRequestDto;
import com.loyai.loyaiproject.dto.response.PayNowResponseDto;
import com.loyai.loyaiproject.service.PayNowService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/ap1/v1/payments/initiate")
public class PayNowController {
    private final PayNowService payNowService;

    @PostMapping()
    public ResponseEntity<PayNowResponseDto> initiatePayment(@RequestBody @Valid PayNowRequestDto payNowRequestDto, HttpServletRequest servletRequest){
        return payNowService.getToken(payNowRequestDto,servletRequest);
    }

}
