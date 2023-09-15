package com.loyai.loyaiproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PaidResponseDto {

    private String userId;
    private int amountPaid;
}
