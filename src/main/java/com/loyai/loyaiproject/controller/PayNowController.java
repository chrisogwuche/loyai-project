package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.request.PaymentRequestDto;
import com.loyai.loyaiproject.dto.response.PayNowResponseDto;
import com.loyai.loyaiproject.service.InitiatePaymentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/ap1/v1/payment/initiate")
public class PayNowController {
    private final InitiatePaymentService initiatePaymentService;

    @PostMapping()
    public ResponseEntity<PayNowResponseDto> initiatePayment(@RequestBody @Valid PaymentRequestDto paymentRequestDto, HttpServletRequest servletRequest){
        return initiatePaymentService.getToken(paymentRequestDto,servletRequest);
    }

}
