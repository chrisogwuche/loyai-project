package com.loyai.loyaiproject.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Meta {
    private String type;
    private String clientId;
    private String client_id;
}
