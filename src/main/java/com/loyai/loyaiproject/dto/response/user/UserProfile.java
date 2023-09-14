package com.loyai.loyaiproject.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile {
    private String clientId;
    private List<String> externalId;
    private String name;
    private String roleId;
    private String phoneNumber;
    private String verificationType;
    private String redirectURL;
    private String createdAt;
    private String updatedAt;
    private String id;
}
