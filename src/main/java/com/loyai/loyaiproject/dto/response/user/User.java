package com.loyai.loyaiproject.dto.response.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
        private String clientId;
        private String userId;
        private String roleId;
        private String email;
        private String phoneNumber;
        private Meta meta;
}
