package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import com.loyai.loyaiproject.dto.response.payment.CheckInvoiceResponseDto;
import com.loyai.loyaiproject.dto.response.payment.VerifyPaymentResponseDto;
import com.loyai.loyaiproject.exception.NotFoundException;
import com.loyai.loyaiproject.kodobe.HttpHeader;
import com.loyai.loyaiproject.kodobe.KodobeURLs;
import com.loyai.loyaiproject.service.PaymentService;
import kong.unirest.JsonObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;



@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RestTemplate restTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    private final String baseUrl = KodobeURLs.BASE_URL;
    private final String transactionVerifyUrl = KodobeURLs.TRANSACTION_VERIFICATION_URL;
    private final String updateInvoiceUrl = KodobeURLs.UPDATE_INVOICE_URL;


    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;


    @Override
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(String transactionId) {

        log.info("transac Id:-----" +transactionId);
        return ResponseEntity.ok(verifyTransactionId(transactionId));
    }

    private PaymentVerifyResponse verifyTransactionId(String transactionId){
        HttpHeader httpHeader = new HttpHeader(clientId,clientSecret);

        String verifyUrl = baseUrl+transactionVerifyUrl+"{id}";

        HttpEntity<String> verifyRequest = new HttpEntity<>(httpHeader.getHeaders());

        ResponseEntity<String> verifyResponse = restTemplate.exchange(verifyUrl,HttpMethod.GET, verifyRequest,String.class,transactionId);

        if(verifyResponse.getStatusCode().value() == 200){
            VerifyPaymentResponseDto verifyPaymentResponseDto = jsonObjectMapper.readValue(verifyResponse.getBody(), VerifyPaymentResponseDto.class);

            log.info("transaction Verification: " + verifyPaymentResponseDto.toString());
            String transactionStatus = verifyPaymentResponseDto.getData().getStatus();

            PaymentVerifyResponse paymentVerifyResponse = new PaymentVerifyResponse();
            String invoiceId = verifyPaymentResponseDto.getData().getInvoiceId();
            int realAmountPaid = (verifyPaymentResponseDto.getData().getAmount())/100;

            if(getInvoice(invoiceId, realAmountPaid) == true &&  transactionStatus.equals("SUCCESS")){
                paymentVerifyResponse.setUserId(verifyPaymentResponseDto.getData().getUserId());
                paymentVerifyResponse.setStatus("TRUE");
                paymentVerifyResponse.setAmountPaid(realAmountPaid);
            }
            else{
                paymentVerifyResponse.setUserId(verifyPaymentResponseDto.getData().getUserId());
                paymentVerifyResponse.setStatus("FAILED");
                paymentVerifyResponse.setAmountPaid(0);
            }

            return paymentVerifyResponse;
        }
        else{
            throw new NotFoundException("error accessing data");
        }
    }

    private boolean getInvoice(String invoiceId,int amount){
        HttpHeader httpHeader = new HttpHeader(clientId,clientSecret);

        HttpEntity<String> updateInvoiceRequest = new HttpEntity<>(httpHeader.getHeaders());
        String updateUrl = baseUrl+updateInvoiceUrl+"{involceId}";
        ResponseEntity<String> invoiceResponse = restTemplate
                .exchange(updateUrl,HttpMethod.GET,updateInvoiceRequest,String.class,invoiceId);

        if(invoiceResponse.getStatusCode().value() != 200){
            throw new NotFoundException("error");
        }

        CheckInvoiceResponseDto invoiceResponseDto =
                jsonObjectMapper.readValue(invoiceResponse.getBody(), CheckInvoiceResponseDto.class);

        log.info("invoiceValue: " +invoiceResponseDto.toString());

        if(invoiceResponseDto.getData().getAmount() == amount && invoiceResponseDto.getData().getStatus().equals("PAID")){
            return true;
        }
        else{
            return false;
        }
    }

}
