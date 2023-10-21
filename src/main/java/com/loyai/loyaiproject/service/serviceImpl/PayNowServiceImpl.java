package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.request.*;
import com.loyai.loyaiproject.dto.response.payment.InvoiceIdDto;
import com.loyai.loyaiproject.dto.response.PayNowResponseDto;
import com.loyai.loyaiproject.dto.response.payment.InitiatePaymentResponseDto;
import com.loyai.loyaiproject.dto.response.user.LoginResponseDto;
import com.loyai.loyaiproject.exception.NotFoundException;
import com.loyai.loyaiproject.exception.ServiceUnAvailableException;
import com.loyai.loyaiproject.kodobe.HttpHeader;
import com.loyai.loyaiproject.kodobe.KodobeURLs;
import com.loyai.loyaiproject.service.PayNowService;
import kong.unirest.JsonObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class PayNowServiceImpl implements PayNowService {
    private final RestTemplate restTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    private final String userServiceUrl = KodobeURLs.USER_SERVICE_URL;
    private final String invoiceServiceUrl = KodobeURLs.INVOICE_SERVICE_URL;
    private final String paymentServiceUrl = KodobeURLs.PAYMENT_SERVICE_URL;
    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    private final String password = "654321";



    @Override
    public ResponseEntity<PayNowResponseDto> getToken(PayNowRequestDto payNowRequestDto) {

        return ResponseEntity.ok(tokensFromLoginUser(payNowRequestDto));
    }

    private PayNowResponseDto tokensFromLoginUser(PayNowRequestDto payNowRequestDto){
        HttpHeader httpHeader = new HttpHeader(clientId,clientSecret);

        createUser(payNowRequestDto.getPhoneNumber(),httpHeader);
        LoginResponseDto loginResponseDto = loginUser(payNowRequestDto.getPhoneNumber(),httpHeader);

        String userId = loginResponseDto.getData().getUser().getUserId();
        String amount = payNowRequestDto.getAmount();
        String productId = payNowRequestDto.getProductId();
        String callbackUrl = payNowRequestDto.getCallbackUrl();

        String invoiceId = createInvoice(amount,userId,productId,httpHeader);

        String paymentUrl = initiatePayment(httpHeader,amount,userId,invoiceId,callbackUrl);

        log.info("payment url " +paymentUrl);

        String token = loginResponseDto.getData().getToken();
        String refreshToken = loginResponseDto.getData().getRefresh();


        PayNowResponseDto payNowResponseDto = new PayNowResponseDto();
        payNowResponseDto.setToken(token);
        payNowResponseDto.setRefreshToken(refreshToken);
        payNowResponseDto.setPaymentUrl(paymentUrl);
        payNowResponseDto.setUserId(userId);
        payNowResponseDto.setInvoiceId(invoiceId);

        return payNowResponseDto;
    }

    private LoginResponseDto loginUser(String phoneNumber, HttpHeader httpHeader){

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPhoneNumber(phoneNumber);
        loginRequestDto.setPassword(password);

        HttpEntity<LoginRequestDto> loginRequest = new HttpEntity<>(loginRequestDto, httpHeader.getHeaders());
        String userLoginUrl = userServiceUrl+"/v1/auths/login";

        ResponseEntity<String> loginResponse = restTemplate.exchange(userLoginUrl,HttpMethod.POST,loginRequest,String.class);

        LoginResponseDto loginResponseDto = jsonObjectMapper.readValue(loginResponse.getBody(),LoginResponseDto.class);

        log.info("LoginResponseDto mapping: " +loginResponseDto.toString());

        return loginResponseDto;
    }

    private void createUser(String phoneNumber,HttpHeader httpHeader){

        var createUser = CreateUserRequestDto.builder()
                .name("Loyai User")
                .password(password)
                .phoneNumber(phoneNumber)
                .build();

        HttpEntity<CreateUserRequestDto> createUserRequest = new HttpEntity<>(createUser, httpHeader.getHeaders());
        String userUrl = userServiceUrl+"/v1/users";

        ResponseEntity<String> createUserResponse = restTemplate.exchange(userUrl, HttpMethod.POST, createUserRequest, String.class);

        log.info("create user response: "+createUserResponse.getBody());
    }

    private String createInvoice(String amount,String userId,String productId,HttpHeader httpHeader){

        InvoiceCreationRequestDto invoiceCreationRequestDto = new InvoiceCreationRequestDto();
        invoiceCreationRequestDto.setProductId(productId);
        invoiceCreationRequestDto.setUserId(userId);
        invoiceCreationRequestDto.setAmount(Integer.parseInt(amount));
        invoiceCreationRequestDto.setValidity(3);
        invoiceCreationRequestDto.setType("one-off");

        HttpEntity<InvoiceCreationRequestDto> invoiceCreationRequest = new HttpEntity<>(invoiceCreationRequestDto, httpHeader.getHeaders());

        String invoiceUrl = invoiceServiceUrl+"/v1/invoices";

        ResponseEntity<String> invoiceCreationResponse = restTemplate.exchange(invoiceUrl, HttpMethod.POST,invoiceCreationRequest,String.class);

        if(invoiceCreationResponse.getStatusCode().value() == 201) {
            InvoiceIdDto invoiceIdDto = jsonObjectMapper.readValue(invoiceCreationResponse.getBody(),InvoiceIdDto.class);
            log.info("invoiceID: "+invoiceIdDto.getData().getId());

            return invoiceIdDto.getData().getId();

        }else if(invoiceCreationResponse.getStatusCode().is4xxClientError()){
            throw new NotFoundException("Can not create invoice. Product not found");
        }
        else{
            throw new ServiceUnAvailableException("service unavailable");
        }
    }

    private String initiatePayment(HttpHeader httpHeader, String amount, String userId, String invoiceId, String callbackUrl){

        log.info("callbackUrl: "+callbackUrl);

        PaymentInitiateRequestDto paymentInitiateRequestDto = new PaymentInitiateRequestDto();
        paymentInitiateRequestDto.setAmount(Integer.parseInt(amount)*100);
        paymentInitiateRequestDto.setUserId(userId);
        paymentInitiateRequestDto.setInvoiceId(invoiceId);
        paymentInitiateRequestDto.setEmail("email@showafrica.app");
        paymentInitiateRequestDto.setRedirectUrl(callbackUrl);

        HttpEntity<PaymentInitiateRequestDto> paymentRequest = new HttpEntity<>(paymentInitiateRequestDto, httpHeader.getHeaders());
        String paymentUrl = paymentServiceUrl+"/v1/flutterwave/initialize";

        ResponseEntity<String> initiatePaymentResponse =
                restTemplate.exchange(paymentUrl,HttpMethod.POST,paymentRequest,String.class);

        if(initiatePaymentResponse.getStatusCode().value() == 200){
            InitiatePaymentResponseDto initiatePaymentResponseDto = jsonObjectMapper.readValue(initiatePaymentResponse.getBody(),InitiatePaymentResponseDto.class);
            log.info("Initiate payment redirectUrl" +initiatePaymentResponseDto.toString());

            return initiatePaymentResponseDto.getData().getUrl();
        }
        else {
            throw new NotFoundException("");
        }
    }

}
