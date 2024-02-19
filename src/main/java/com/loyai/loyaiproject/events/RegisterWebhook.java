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
    @Value("${loyai_baseUrl}")
    private String loyaiBaseUrl;
    @Value("${eventUrl}")
    private String eventUrl;



    @PostConstruct
    private void onStartUp(){
        saveWebhookAPI();
    }

    private void saveWebhookAPI() {

        log.info("Saving webhook------");

        HttpHeader httpHeader = new HttpHeader(clientId,clientSecret);
        String loyaiWebhookUrl = loyaiBaseUrl +"/api/v1/webhook/game";

        RegisterWebhookRequestDto registerWebhook = new RegisterWebhookRequestDto();
        registerWebhook.setEvent("game.updated.won");
        registerWebhook.setWebhook(loyaiWebhookUrl);

        HttpEntity<RegisterWebhookRequestDto> registerWebhookRequest = new HttpEntity<>(registerWebhook, httpHeader.getHeaders());
        String url =eventUrl+"/v1/http";

        log.info(" register webhook request..: "+registerWebhookRequest);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST,registerWebhookRequest,String.class);
        log.info(" register webhook response..: "+response);

        if(response.getStatusCode().value()==201){
            log.info("Webhook saved");
        }
        else{
            log.info("Failed saving webhook !!");
        }
    }
}
