package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/webhook/game")
@CrossOrigin("*")
@Slf4j
public class WebhookController {
    private final WebhookService webhookService;

    @PostMapping
    public ResponseEntity<String> gameUpdate(@RequestBody String requestBody) {
        return webhookService.setWebhookData(requestBody);
    }

}
