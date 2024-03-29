package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.request.AddMoneyToWalletRequest;
import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import com.loyai.loyaiproject.dto.response.invoice.CheckInvoiceResponseDto;
import com.loyai.loyaiproject.exception.NotFoundException;
import com.loyai.loyaiproject.exception.ServiceUnAvailableException;
import com.loyai.loyaiproject.kodobe.HttpHeader;
import com.loyai.loyaiproject.model.Users;
import com.loyai.loyaiproject.repository.UsersRepository;
import com.loyai.loyaiproject.service.PaymentService;
import jakarta.transaction.Transactional;
import kong.unirest.JsonObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RestTemplate restTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    private final UsersRepository usersRepository;


    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${FT_applicationId}")
    private String FTapplicationId;
    @Value("${clientLedgerId}")
    private String clientLedgerId;
    @Value("${billingUrl}")
    private String billingUrl;
    @Value("${invoiceUrl}")
    private String invoiceUrl;



    @Transactional
    @Override
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(String userId, String invoiceId) {
        return paymentVerification(userId,invoiceId);
    }

    private ResponseEntity<PaymentVerifyResponse> paymentVerification(String userId, String invoiceId) {
        HttpHeader httpHeader = new HttpHeader(clientId, clientSecret);
        CheckInvoiceResponseDto checkInvoiceResponseDto = getInvoice(invoiceId, httpHeader);
        boolean verifiedPayment = verifyPaymentWithInvoiceAndId(checkInvoiceResponseDto, userId);
        int amountPaid = checkInvoiceResponseDto.getData().getAmount()/100;
        PaymentVerifyResponse paymentVerifyResponse = new PaymentVerifyResponse();

        if (verifiedPayment) {
            paymentVerifyResponse.setStatus("SUCCESS");
            paymentVerifyResponse.setAmountPaid(String.valueOf(amountPaid));
            saveUserToDatabase(userId,amountPaid,invoiceId);  /* saves the user and the airtime amount bought in the database*/
            int noOfChances = amountPaid/100;
            addMoneyToWallet(noOfChances,userId,httpHeader);
            return new ResponseEntity<>(paymentVerifyResponse,HttpStatus.OK);
        }
        paymentVerifyResponse.setStatus("FAILED");
        paymentVerifyResponse.setAmountPaid("0");
        return new ResponseEntity<>(paymentVerifyResponse,HttpStatus.NOT_FOUND);
    }

    private boolean verifyPaymentWithInvoiceAndId(CheckInvoiceResponseDto invoiceResponseDto, String userId) {
        boolean isIdVerified = invoiceResponseDto.getData().getUserId().equals(userId);

        if(!isIdVerified){
            throw new NotFoundException("error verifying payment with userId");
        }
        String invoiceStatus = invoiceResponseDto.getData().getStatus();
        log.info("invoiceValue: " + invoiceResponseDto.getData());
        return invoiceStatus.equals("PAID");
    }

    private CheckInvoiceResponseDto getInvoice(String invoiceId, HttpHeader httpHeader) {
        HttpEntity<String> updateInvoiceRequest = new HttpEntity<>(httpHeader.getHeaders());
        String updateUrl = invoiceUrl+"/v1/invoices/"+"{involceId}";

        log.info("invoice request------>" +updateInvoiceRequest);
        ResponseEntity<String> invoiceResponse = restTemplate
                .exchange(updateUrl, HttpMethod.GET, updateInvoiceRequest, String.class, invoiceId);
        log.info("GET invoice http response----->" +invoiceResponse);

        if (invoiceResponse.getStatusCode().value() != 200) {
            throw new NotFoundException("error verifying invoice with invoiceId");
        }
        return jsonObjectMapper.readValue(invoiceResponse.getBody(), CheckInvoiceResponseDto.class);
    }

    private void saveUserToDatabase(String userId,int amountPaid,String invoiceId){
        Users user = new Users();
        user.setUserId(userId);
        user.setAirtimeBought(amountPaid);
        user.setInvoiceId(invoiceId);
        user.setCreatedAt(LocalDateTime.now());

        log.info(" saving user-----");
        usersRepository.save(user);
    }

    private void addMoneyToWallet(int amount, String userId, HttpHeader httpHeader){
        AddMoneyToWalletRequest addMoneyToWalletRequest = new AddMoneyToWalletRequest();
        addMoneyToWalletRequest.setAmount(amount);
        addMoneyToWalletRequest.setNarration("Funding wallet with " +amount +" Chances");
        addMoneyToWalletRequest.setApplicationId(FTapplicationId);
        addMoneyToWalletRequest.setLedgerId(clientLedgerId);
        addMoneyToWalletRequest.setProductId("ChanceFundTransferProduct");
        addMoneyToWalletRequest.setBeneficiaryCustomerId(userId);

        HttpEntity<AddMoneyToWalletRequest> addRequest = new HttpEntity<>(addMoneyToWalletRequest, httpHeader.getHeaders());
        String url = billingUrl +"/wallet/product/transfer";
        log.info("wallet request----->" +addRequest);
        ResponseEntity<String> walletResponse = restTemplate.exchange(url,HttpMethod.POST,addRequest,String.class);
        log.info("wallet response: " +walletResponse.getBody());

        if(walletResponse.getStatusCode().value() !=200){
            throw new ServiceUnAvailableException(new RuntimeException().getMessage());
        }
    }
}
