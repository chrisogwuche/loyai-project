package com.loyai.loyaiproject.events;

import com.loyai.loyaiproject.dto.request.RegisterWebhookRequestDto;
import com.loyai.loyaiproject.kodobe.HttpHeader;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegisterWebhook {
    private final RestTemplate restTemplate;

    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${app_baseUrl}")
    private String appBaseUrl;
    @Value("${baseUrl}")
    private String baseUrl;



    @PostConstruct
    private void onStartUp(){
        saveWebhookAPI();
    }

    private void saveWebhookAPI() {

        log.info("Saving webhook------");

        HttpHeader httpHeader = new HttpHeader(clientId,clientSecret);
        String webhookUrl = appBaseUrl+"/api/v1/webhook/game";

        RegisterWebhookRequestDto registerWebhook = new RegisterWebhookRequestDto();
        registerWebhook.setEvent("game.updated.won");
        registerWebhook.setWebhook(webhookUrl);

        HttpEntity<RegisterWebhookRequestDto> registerWebhookRequest = new HttpEntity<>(registerWebhook, httpHeader.getHeaders());
        String saveWebhookUrl = baseUrl+"/events/v1/http";

        ResponseEntity<String> response = restTemplate.exchange(saveWebhookUrl, HttpMethod.POST,registerWebhookRequest,String.class);

        if(response.getStatusCode().value()==201){

            log.info("Webhook saved");
        }
        else{
            log.info("Failed saving webhook !!");
        }
    }
}
