package com.loyai.loyaiproject.service;

import com.loyai.loyaiproject.dto.response.product.ProductResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ProductService {

    ResponseEntity<ProductResponseDto> getAllProducts();

}
