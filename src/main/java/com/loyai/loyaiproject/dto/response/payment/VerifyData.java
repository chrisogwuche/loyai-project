package com.loyai.loyaiproject.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class VerifyData {
     private String status;
     private String userId;
     private int amount;
     private String invoiceId;

}
