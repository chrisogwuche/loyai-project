package com.loyai.loyaiproject.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterWebhookRequestDto {

     private String event;
     private String webhook;
}
