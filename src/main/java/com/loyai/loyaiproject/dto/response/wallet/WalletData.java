package com.loyai.loyaiproject.dto.response.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletData {

    private String userId;
    private int balance;
}
