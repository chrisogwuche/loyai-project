package com.loyai.loyaiproject.kodobe;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;

@Data
@NoArgsConstructor
public class HttpHeader {

    private HttpHeaders headers;

    public HttpHeader(String clientId,String secret){
        headers = new HttpHeaders();
        headers.set("client-id",clientId);
        headers.set("secret",secret);
    }

    public HttpHeader(String clientId,String secret,String bearerToken){
        headers = new HttpHeaders();
        headers.set("client-id",clientId);
        headers.set("secret",secret);
        headers.set("Authorization", "Bearer " +bearerToken);
    }

}
