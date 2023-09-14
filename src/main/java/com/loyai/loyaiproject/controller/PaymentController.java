package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import com.loyai.loyaiproject.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payment/verify")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping()
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(@RequestParam("tx_ref") String transactionRef){

        return paymentService.verifyPayment(transactionRef);
    }
}
