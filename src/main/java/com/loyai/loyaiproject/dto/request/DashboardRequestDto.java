package com.loyai.loyaiproject.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRequestDto {

    @NotNull(message = "enter a bearerToken")
    private String bearerToken;
}
