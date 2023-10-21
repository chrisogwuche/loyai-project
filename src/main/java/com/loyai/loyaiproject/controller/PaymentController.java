package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import com.loyai.loyaiproject.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
//@CrossOrigin("http://localhost:5173/")
@CrossOrigin("*")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/verify")
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(@RequestParam("invoice_id") String invoiceId,
                                           @RequestParam("user_id") String userId){

        return paymentService.verifyPayment(userId, invoiceId);
    }

}
