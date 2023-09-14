package com.loyai.loyaiproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaymentInitiateRequestDto {

    private String email;
    private String redirectUrl;
    private int amount;
    private String userId;
    private String invoiceId;
}
