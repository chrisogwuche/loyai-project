package com.loyai.loyaiproject.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductData {

    private String id;
    private String client_id;
    private String name;
    private int amount;
    private String description;
    private String parent_id;
    private Meta meta;
    private String deleted_at;
    private String created_at;
    private String updated_at;
    private String external_product_id;
    private String type;
    private String image;
    private String parent;
}
