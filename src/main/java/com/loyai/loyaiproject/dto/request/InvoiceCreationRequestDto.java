package com.loyai.loyaiproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceCreationRequestDto {

    private String productId;
    private String userId;
    private int amount;
    private int validity;
    private String type;
}
