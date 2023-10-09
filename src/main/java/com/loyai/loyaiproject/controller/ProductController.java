package com.loyai.loyaiproject.controller;

import com.loyai.loyaiproject.dto.response.product.ProductResponseDto;
import com.loyai.loyaiproject.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@CrossOrigin("http://localhost:5173/")
public class ProductController {

    private final ProductService productService;

    @GetMapping("/all")
    public ResponseEntity<ProductResponseDto> getAllProduct(){

        return productService.getAllProducts();
    }
}
