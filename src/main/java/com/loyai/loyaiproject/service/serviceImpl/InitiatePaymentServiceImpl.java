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
import com.loyai.loyaiproject.service.InitiatePaymentService;
import jakarta.servlet.http.HttpServletRequest;
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
public class InitiatePaymentServiceImpl implements InitiatePaymentService {
    private final RestTemplate restTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    private final String baseUrl = KodobeURLs.BASE_URL;
    private final String createUserUrl = KodobeURLs.CREATE_USER_URL;
    private final String loginUrl = KodobeURLs.LOGIN_URL;
    private final String invoiceCreationUrl = KodobeURLs.INVOICE_CREATION_URL;
    private final String initiatePaymentUrl = KodobeURLs.INITIATE_PAYMENT_FLUTTER_URL;
    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    private final String password = "654321";



    @Override
    public ResponseEntity<PayNowResponseDto> getToken(PaymentRequestDto paymentRequestDto, HttpServletRequest servletRequest) {

        return ResponseEntity.ok(tokensFromLoginUser(paymentRequestDto,servletRequest));
    }

    private PayNowResponseDto tokensFromLoginUser(PaymentRequestDto paymentRequestDto, HttpServletRequest servletRequest){
        HttpHeader httpHeader = new HttpHeader(clientId,clientSecret);

        createUser(paymentRequestDto.getPhoneNumber(),httpHeader);
        LoginResponseDto loginResponseDto = loginUser(paymentRequestDto.getPhoneNumber(),httpHeader);

        String userId = loginResponseDto.getData().getUser().getUserId();
        String amount = paymentRequestDto.getAmount();
        String productId = paymentRequestDto.getProductId();

        String invoiceId = createInvoice(amount,userId,productId,httpHeader);

        String url = initiatePayment(httpHeader,amount,userId,invoiceId,servletRequest);

        log.info("sevelet request " +url);

        String token = loginResponseDto.getData().getToken();
        String refreshToken = loginResponseDto.getData().getRefresh();


        PayNowResponseDto payNowResponseDto = new PayNowResponseDto();
        payNowResponseDto.setToken(token);
        payNowResponseDto.setRefreshToken(refreshToken);
        payNowResponseDto.setRedirectUrl(url);

        return payNowResponseDto;
    }

    private LoginResponseDto loginUser(String phoneNumber, HttpHeader httpHeader){

        LoginRequestDto loginRequestDto = new LoginRequestDto();
        loginRequestDto.setPhoneNumber(phoneNumber);
        loginRequestDto.setPassword(password);

        HttpEntity<LoginRequestDto> loginRequest = new HttpEntity<>(loginRequestDto, httpHeader.getHeaders());
        String userLoginUrl = baseUrl+loginUrl;

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
        String userUrl = baseUrl+createUserUrl;

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

        String invoiceUrl = baseUrl+invoiceCreationUrl;

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

    private String initiatePayment(HttpHeader httpHeader,String amount,String userId,String invoiceId,HttpServletRequest servletRequest){

        String url = "http://"+servletRequest.getServerName()+":"+servletRequest.getServerPort()+servletRequest.getContextPath();
        String redirectUrl = url+"/api/v1/payment/verify";

        log.info("redirectUrl: "+redirectUrl + "   url: "+url);

        PaymentInitiateRequestDto paymentInitiateRequestDto = new PaymentInitiateRequestDto();
        paymentInitiateRequestDto.setAmount(Integer.parseInt(amount)*100);
        paymentInitiateRequestDto.setUserId(userId);
        paymentInitiateRequestDto.setInvoiceId(invoiceId);
        paymentInitiateRequestDto.setEmail("email@showafrica.app");
        paymentInitiateRequestDto.setRedirectUrl(redirectUrl);

        HttpEntity<PaymentInitiateRequestDto> paymentRequest = new HttpEntity<>(paymentInitiateRequestDto, httpHeader.getHeaders());
        String paymentUrl = baseUrl+initiatePaymentUrl;

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
