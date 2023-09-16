package com.loyai.loyaiproject.service.serviceImpl;

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


@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RestTemplate restTemplate;
    private final JsonObjectMapper jsonObjectMapper;
    private final String paymentServiceUrl = KodobeURLs.PAYMENT_SERVICE_URL;
    private final String invoiceServiceUrl = KodobeURLs.INVOICE_SERVICE_URL;


    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;


    @Override
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(String transactionId, String userId) {

        log.info("transac Id:-----" + transactionId);

        return paymentVerification(transactionId,userId);
    }

    private ResponseEntity<PaymentVerifyResponse> paymentVerification(String transactionId, String userId) {
        HttpHeader httpHeader = new HttpHeader(clientId, clientSecret);

        VerifyPaymentResponseDto verifyPaymentResponseDto = verifyTransactionId(transactionId, httpHeader);

        String invoiceId = verifyPaymentResponseDto.getData().getInvoiceId();
        int realAmountPaid = (verifyPaymentResponseDto.getData().getAmount()) / 100;

        CheckInvoiceResponseDto checkInvoiceResponseDto = getInvoice(invoiceId, httpHeader);

        boolean isAmountVerified = verifyAmountPaidAndInvoiceStatus(checkInvoiceResponseDto, realAmountPaid);
        boolean isIdVerified = checkInvoiceResponseDto.getData().getUserId().equals(userId);
        String transactionStatus = verifyPaymentResponseDto.getData().getStatus();

        PaymentVerifyResponse paymentVerifyResponse = new PaymentVerifyResponse();

        if (isAmountVerified && transactionStatus.equals("SUCCESS") && isIdVerified) {
            paymentVerifyResponse.setStatus("SUCCESS");
            paymentVerifyResponse.setAmountPaid(checkInvoiceResponseDto.getData().getAmount());
            paymentVerifyResponse.setUserId(checkInvoiceResponseDto.getData().getUserId());

            return new ResponseEntity<>(paymentVerifyResponse,HttpStatus.OK);
        }

        throw new NotFoundException("No transaction found for this user");
    }

    private VerifyPaymentResponseDto verifyTransactionId(String transactionId, HttpHeader httpHeader) {

        String verifyUrl = paymentServiceUrl+"/v1/flutterwave/verify/"+"{id}";

        HttpEntity<String> verifyRequest = new HttpEntity<>(httpHeader.getHeaders());

        ResponseEntity<String> verifyResponse = restTemplate.exchange(verifyUrl, HttpMethod.GET, verifyRequest, String.class, transactionId);

        if (verifyResponse.getStatusCode().value() == 200) {
            VerifyPaymentResponseDto verifyPaymentResponseDto = jsonObjectMapper.readValue(verifyResponse.getBody(), VerifyPaymentResponseDto.class);

            log.info("transaction Verification: " + verifyPaymentResponseDto.toString());

            return verifyPaymentResponseDto;
        } else {
            throw new NotFoundException("error verifying payment with tx_ref");
        }
    }

    private boolean verifyAmountPaidAndInvoiceStatus(CheckInvoiceResponseDto invoiceResponseDto, int amount) {

        int invoiceAmount = invoiceResponseDto.getData().getAmount();
        String invoiceStatus = invoiceResponseDto.getData().getStatus();

        log.info("invoiceValue: " + invoiceResponseDto.getData());

        if (invoiceAmount == amount && invoiceStatus.equals("PAID")) {
            return true;
        } else {
            return false;
        }
    }

    private CheckInvoiceResponseDto getInvoice(String invoiceId, HttpHeader httpHeader) {

        HttpEntity<String> updateInvoiceRequest = new HttpEntity<>(httpHeader.getHeaders());
        String updateUrl = invoiceServiceUrl+"/v1/invoices/"+"{involceId}";

        ResponseEntity<String> invoiceResponse = restTemplate
                .exchange(updateUrl, HttpMethod.GET, updateInvoiceRequest, String.class, invoiceId);

        if (invoiceResponse.getStatusCode().value() != 200) {
            throw new NotFoundException("error verifying invoice");
        }

        CheckInvoiceResponseDto invoiceResponseDto =
                jsonObjectMapper.readValue(invoiceResponse.getBody(), CheckInvoiceResponseDto.class);

        return invoiceResponseDto;
    }

}




