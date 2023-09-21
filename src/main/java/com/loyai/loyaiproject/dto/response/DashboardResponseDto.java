package com.loyai.loyaiproject.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponseDto {
    private int rewardPool;
    private int userChances;
    private String gamePlayUrl;
}
