package com.loyai.loyaiproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCredentialRequestDto {
     private String publicKey;
     private String secretKey;
}
