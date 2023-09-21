package com.loyai.loyaiproject.configuration;

import com.loyai.loyaiproject.exception.RestTemplateErrorHandler;
import jakarta.servlet.http.HttpServletRequest;
import kong.unirest.JsonObjectMapper;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(){
        RestTemplate restTemplate = new RestTemplateBuilder()
                .errorHandler(new RestTemplateErrorHandler())
                .build();

        return restTemplate;
    }
    @Bean
    public JsonObjectMapper jsonObjectMapper(){
        return new JsonObjectMapper();
    }

}
