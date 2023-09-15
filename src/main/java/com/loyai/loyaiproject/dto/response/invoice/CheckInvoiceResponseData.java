package com.loyai.loyaiproject.dto.response.invoice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CheckInvoiceResponseData {
    private int amount;
    private String status;
    private String userId;
}
