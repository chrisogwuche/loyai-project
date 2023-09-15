package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.response.PaidResponseDto;
import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import com.loyai.loyaiproject.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping("/verify")
    public RedirectView verifyPayment(@RequestParam("tx_ref") String transactionRef, RedirectAttributes redirectAttributes){

        return paymentService.verifyPayment(transactionRef, redirectAttributes);
    }

    @GetMapping("/user")
    public ResponseEntity<PaidResponseDto> verifyPayment(@RequestParam("invoiceId") String invoiceId
            , @RequestParam("userId") String userId){

        return paymentService.getPaymentInfo(invoiceId,userId);
    }
}
