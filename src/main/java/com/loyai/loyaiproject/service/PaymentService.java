package com.loyai.loyaiproject.service;

import com.loyai.loyaiproject.dto.response.PaidResponseDto;
import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

@Service
public interface PaymentService {
    RedirectView verifyPayment(String transactionId, RedirectAttributes redirectAttributes);

    ResponseEntity<PaidResponseDto> getPaymentInfo(String invoiceId, String userId);
}
