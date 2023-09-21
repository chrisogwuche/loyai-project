package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.request.WebhookRequestDto;
import com.loyai.loyaiproject.service.WebhookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webhook/game")
@Slf4j
public class WebhookController {
    private final WebhookService webhookService;


    @PostMapping("")
    public ResponseEntity<WebhookRequestDto> gameUpdate(@RequestBody @Null WebhookRequestDto webhookRequestDto){

        log.info("webhook response: "+webhookRequestDto.toString());

        return webhookService.setWebhookData(webhookRequestDto);
    }

}
