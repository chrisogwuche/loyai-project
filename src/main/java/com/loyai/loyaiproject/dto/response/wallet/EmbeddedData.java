package com.loyai.loyaiproject.dto.response.wallet;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmbeddedData {

    private List<WalletData> wallets;
}
