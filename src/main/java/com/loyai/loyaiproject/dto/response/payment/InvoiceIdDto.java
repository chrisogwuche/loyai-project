package com.loyai.loyaiproject.dto.response.payment;

import com.loyai.loyaiproject.dto.response.payment.InvoiceDataDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceIdDto {
    private InvoiceDataDto data;
}
