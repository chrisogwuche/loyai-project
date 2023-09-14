package com.loyai.loyaiproject.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerifyErrorResponse {
    private String status;
    private String userId;
}
