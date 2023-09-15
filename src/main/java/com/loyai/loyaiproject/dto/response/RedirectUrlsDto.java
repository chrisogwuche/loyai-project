package com.loyai.loyaiproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RedirectUrlsDto {
    private String verifyPaymentUrl;
    private String paymentUrl;
}
