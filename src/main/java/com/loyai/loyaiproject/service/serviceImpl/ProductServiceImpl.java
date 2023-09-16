package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.response.product.ProductResponseDto;
import com.loyai.loyaiproject.kodobe.HttpHeader;
import com.loyai.loyaiproject.kodobe.KodobeURLs;
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
    private final String productServiceUrl = KodobeURLs.PRODUCT_SERVICE_URL;
    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;

    @Override
    public ResponseEntity<ProductResponseDto> getAllProducts() {

        HttpHeader httpHeader = new HttpHeader(clientId,clientSecret);

        HttpEntity<String> request = new HttpEntity<>(httpHeader.getHeaders());
        String getProductUrl = productServiceUrl+"/v1/product";
        ResponseEntity<String> response = restTemplate.exchange(getProductUrl, HttpMethod.GET,request,String.class);

        log.info("product Response "+response.getBody());

        ProductResponseDto productResponseDTO = jsonObjectMapper.readValue(response.getBody(), ProductResponseDto.class);

        log.info("Products---: " + productResponseDTO.toString());

        return ResponseEntity.ok(productResponseDTO);
    }

}
