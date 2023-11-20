package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.response.product.ProductResponseDto;
import com.loyai.loyaiproject.exception.NotFoundException;
import com.loyai.loyaiproject.kodobe.HttpHeader;
import com.loyai.loyaiproject.service.ProductService;
import kong.unirest.JsonObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final RestTemplate restTemplate;
    private final JsonObjectMapper jsonObjectMapper;

    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${productUrl}")
    private String productUrl;

    @Override
    public ResponseEntity<ProductResponseDto> getAllProducts() {
        HttpHeader httpHeader = new HttpHeader(clientId,clientSecret);
        HttpEntity<String> request = new HttpEntity<>(httpHeader.getHeaders());
        String url = productUrl +"/v1/product";

        log.info("product request..: "+request);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET,request,String.class);
        log.info("product Response "+response.getBody());

        if(response.getStatusCode().value() == 200){
            ProductResponseDto productResponseDTO = jsonObjectMapper.readValue(response.getBody(), ProductResponseDto.class);
            return ResponseEntity.ok(productResponseDTO);
        }
        else {
            throw new NotFoundException(response.getBody());
        }
    }
}
