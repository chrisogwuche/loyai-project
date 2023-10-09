package com.loyai.loyaiproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMoneyToWalletRequest {

    private String beneficiaryCustomerId;
    private int amount;
    private String applicationId;
    private String narration;
    private String productId;
    private String ledgerId;
}
