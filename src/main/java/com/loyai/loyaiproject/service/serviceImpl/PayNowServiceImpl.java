package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.request.*;
import com.loyai.loyaiproject.dto.response.payment.InvoiceIdDto;
import com.loyai.loyaiproject.dto.response.PayNowResponseDto;
import com.loyai.loyaiproject.dto.response.payment.InitiatePaymentResponseDto;
import com.loyai.loyaiproject.dto.response.user.LoginResponseDto;
import com.loyai.loyaiproject.exception.NotFoundException;
import com.loyai.loyaiproject.exception.ServiceUnAvailableException;
import com.loyai.loyaiproject.kodobe.HttpHeader;
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

    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${userUrl}")
    private String userUrl;
    @Value("${invoiceUrl}")
    private String invoiceUrl;
    @Value("${paymentUrl}")
    private String paymentUrl;
    @Value("${initiatePaymentEmail}")
    private String initiatePaymentEmail;
    @Value("${invoiceValidity}")
    private String invoiceValidity;
    @Value("${invoiceType}")
    private String invoiceType;



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
        loginRequestDto.setPassword(phoneNumber);

        HttpEntity<LoginRequestDto> loginRequest = new HttpEntity<>(loginRequestDto, httpHeader.getHeaders());
        String url = userUrl+"/v1/auths/login";
        log.info("login request...: "+loginRequest);
        ResponseEntity<String> loginResponse = restTemplate.exchange(url,HttpMethod.POST,loginRequest,String.class);
        log.info("login response..: "+loginResponse);

        if(loginResponse.getStatusCode().value() == 200){
            return jsonObjectMapper.readValue(loginResponse.getBody(),LoginResponseDto.class);
        }
        throw new NotFoundException(loginResponse.getBody());
    }

    private void createUser(String phoneNumber,HttpHeader httpHeader){
        var createUser = CreateUserRequestDto.builder()
                .name("Loyai User")
                .password(phoneNumber)
                .phoneNumber(phoneNumber)
                .build();

        HttpEntity<CreateUserRequestDto> createUserRequest = new HttpEntity<>(createUser, httpHeader.getHeaders());
        String url = userUrl +"/v1/users";
        log.info("create user request...: "+createUserRequest);
        ResponseEntity<String> createUserResponse = restTemplate.exchange(url, HttpMethod.POST, createUserRequest, String.class);
        log.info("create user response...: "+createUserResponse);
    }

    private String createInvoice(String amount,String userId,String productId,HttpHeader httpHeader){
        InvoiceCreationRequestDto invoiceCreationRequestDto = new InvoiceCreationRequestDto();
        invoiceCreationRequestDto.setProductId(productId);
        invoiceCreationRequestDto.setUserId(userId);
        invoiceCreationRequestDto.setAmount(Integer.parseInt(amount));
        invoiceCreationRequestDto.setValidity(Integer.parseInt(invoiceValidity));
        invoiceCreationRequestDto.setType(invoiceType);

        HttpEntity<InvoiceCreationRequestDto> invoiceCreationRequest = new HttpEntity<>(invoiceCreationRequestDto, httpHeader.getHeaders());
        String url =invoiceUrl+"/v1/invoices";
        log.info("invoice creation request...: "+invoiceCreationRequest);
        ResponseEntity<String> invoiceCreationResponse = restTemplate.exchange(url, HttpMethod.POST,invoiceCreationRequest,String.class);
        log.info("invoice creation response...: "+invoiceCreationResponse);

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
        paymentInitiateRequestDto.setEmail(initiatePaymentEmail);
        paymentInitiateRequestDto.setRedirectUrl(callbackUrl);

        HttpEntity<PaymentInitiateRequestDto> paymentRequest =
                new HttpEntity<>(paymentInitiateRequestDto, httpHeader.getHeaders());
        String url = paymentUrl +"/v1/flutterwave/initialize";

        log.info("payment request..: "+paymentRequest);
        ResponseEntity<String> initiatePaymentResponse =
                restTemplate.exchange(url,HttpMethod.POST,paymentRequest,String.class);
        log.info("payment response..: "+initiatePaymentResponse);

        if(initiatePaymentResponse.getStatusCode().value() == 200){
            InitiatePaymentResponseDto initiatePaymentResponseDto = jsonObjectMapper.readValue(initiatePaymentResponse.getBody(),InitiatePaymentResponseDto.class);
            return initiatePaymentResponseDto.getData().getUrl();
        }
        else {
            throw new NotFoundException(initiatePaymentResponse.getBody());
        }
    }

}
