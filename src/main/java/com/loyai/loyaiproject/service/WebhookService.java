package com.loyai.loyaiproject.service;

import com.loyai.loyaiproject.dto.request.WebhookRequestDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface WebhookService {
    ResponseEntity<WebhookRequestDto> setWebhookData(WebhookRequestDto webhookRequestDto);

}
