package com.loyai.loyaiproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PayNowResponseDto {
    private String token;
    private String refreshToken;
    private String paymentRedirectUrl;
    private String userId;
    private String verifyPaymentUrl;
}
