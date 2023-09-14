package com.loyai.loyaiproject.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseData {
    private String actionType;
    private User user;
    private String token;
    private String refresh;
}
