package com.loyai.loyaiproject.dto.response.payment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ResponseData {

    private String url;
    private String reference;
    private List<Action> actions;
    private boolean redirect;
}
