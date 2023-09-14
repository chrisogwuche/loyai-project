package com.loyai.loyaiproject.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@lombok.Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseDto {

    private int total;
    private int pages;
    private int page;
    private List<ProductData> data;
}
