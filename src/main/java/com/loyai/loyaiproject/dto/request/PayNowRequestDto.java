package com.loyai.loyaiproject.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PayNowRequestDto {
    @Size(min = 11, max = 13, message = "Must be a 11-digit string")
    @Pattern(regexp = "\\d+", message = "Only digits are allowed")
    private String phoneNumber;

    @NotNull(message = "product_id should not be null")
    private String productId;

    @NotNull(message = "amount should not be null")
    @Pattern(regexp = "\\d+", message = "Only digits are allowed")
    private String Amount;

    @URL(message = "url should start with http:// or https://")
    private String callbackUrl;

}
