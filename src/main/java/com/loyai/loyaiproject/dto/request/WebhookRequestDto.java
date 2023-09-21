package com.loyai.loyaiproject.dto.request;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class WebhookRequestDto {

    private String event;
    private String clientId;

    private WebhookData data;
    private String deliveryId;

}
