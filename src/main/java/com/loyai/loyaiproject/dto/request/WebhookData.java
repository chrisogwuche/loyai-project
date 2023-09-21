package com.loyai.loyaiproject.dto.request;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WebhookData {
     private String userId;
    private String clientId;
    private String userInput;
    private String gameToken;
    private String transactionRef;
    private String status;
    private String gameInstanceId;
    private String drawId;
    private String prizeId;
    private String prizeLabel;
    private String prizeAmount;
    private String appId;

}
