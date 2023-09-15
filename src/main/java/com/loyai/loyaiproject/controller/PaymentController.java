package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.response.PaidResponseDto;
import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import com.loyai.loyaiproject.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/verify")
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(@RequestParam("tx_ref") String transactionRef){

        return paymentService.verifyPayment(transactionRef);
    }

    @GetMapping("/user")
    public ResponseEntity<PaidResponseDto> verifyPayment(@RequestParam("invoiceId") String invoiceId
            , @RequestParam("userId") String userId){

        return paymentService.getPaymentInfo(invoiceId,userId);
    }
}
