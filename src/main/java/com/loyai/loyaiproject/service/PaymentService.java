package com.loyai.loyaiproject.service;

import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface PaymentService {
    ResponseEntity<PaymentVerifyResponse>  verifyPayment(String userId, String invoiceId);

}
