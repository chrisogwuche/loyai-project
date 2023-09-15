package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.response.PaidResponseDto;
import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import com.loyai.loyaiproject.dto.response.invoice.CheckInvoiceResponseDto;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;


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
    public RedirectView verifyPayment(String transactionId, RedirectAttributes redirectAttributes) {

        log.info("transac Id:-----" +transactionId);
//        return ResponseEntity.ok(verifyTransactionId(transactionId));

        PaymentVerifyResponse paymentVerifyResponse = verifyTransactionId(transactionId);

        if(paymentVerifyResponse.getStatus().equals("TRUE")){

            redirectAttributes.addAttribute("invoiceId",paymentVerifyResponse.getInvoiceId());


            return new RedirectView("https://loyai-work.vercel.app/");
        }
        else{

            return new RedirectView("https://google.com");
        }
    }

    @Override
    public ResponseEntity<PaidResponseDto> getPaymentInfo(String invoiceId, String userId) {

        CheckInvoiceResponseDto invoiceResponseDto = getInvoice(invoiceId);

        boolean paidStatus = invoiceResponseDto.getData().getStatus().equals("PAID");
        boolean userIdVerifyStatus = invoiceResponseDto.getData().getUserId().equals(userId);

        if(paidStatus && userIdVerifyStatus){
            PaidResponseDto paidResponseDto = new PaidResponseDto();
            paidResponseDto.setAmountPaid(invoiceResponseDto.getData().getAmount());
            paidResponseDto.setUserId(invoiceResponseDto.getData().getUserId());

            return ResponseEntity.ok(paidResponseDto);
        }
        else{
            throw new NotFoundException("userId do no match with the invoice userId");
        }

    }

    private PaymentVerifyResponse verifyTransactionId(String transactionId){
        HttpHeader httpHeader = new HttpHeader(clientId,clientSecret);

        String verifyUrl = baseUrl+transactionVerifyUrl+"{id}";

        HttpEntity<String> verifyRequest = new HttpEntity<>(httpHeader.getHeaders());

        ResponseEntity<String> verifyResponse = restTemplate.exchange(verifyUrl,HttpMethod.GET, verifyRequest,String.class,transactionId);
        PaymentVerifyResponse paymentVerifyResponse = new PaymentVerifyResponse();

        if(verifyResponse.getStatusCode().value() == 200){
            VerifyPaymentResponseDto verifyPaymentResponseDto = jsonObjectMapper.readValue(verifyResponse.getBody(), VerifyPaymentResponseDto.class);

            log.info("transaction Verification: " + verifyPaymentResponseDto.toString());
            String transactionStatus = verifyPaymentResponseDto.getData().getStatus();

            String invoiceId = verifyPaymentResponseDto.getData().getInvoiceId();
            int realAmountPaid = (verifyPaymentResponseDto.getData().getAmount())/100;

            if(verifyAmountPaid(invoiceId, realAmountPaid) == true &&  transactionStatus.equals("SUCCESS")){
                paymentVerifyResponse.setStatus("TRUE");
                paymentVerifyResponse.setInvoiceId(invoiceId);
            }
            else{
                paymentVerifyResponse.setStatus("FAILED");
            }
        }
        else {
            paymentVerifyResponse.setStatus("FAILED");
        }
        return paymentVerifyResponse;
    }

    private boolean verifyAmountPaid(String invoiceId,int amount){

        CheckInvoiceResponseDto invoiceResponseDto = getInvoice(invoiceId);

        log.info("invoiceValue: " +invoiceResponseDto.toString());

        if(invoiceResponseDto.getData().getAmount() == amount && invoiceResponseDto.getData().getStatus().equals("PAID")){
            return true;
        }
        else{
            return false;
        }
    }

    private CheckInvoiceResponseDto getInvoice(String invoiceId){
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

        return invoiceResponseDto;
    }

}
