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
    private final String baseUrl = KodobeURLs.BASE_URL;
    private final String transactionVerifyUrl = KodobeURLs.TRANSACTION_VERIFICATION_URL;
    private final String updateInvoiceUrl = KodobeURLs.UPDATE_INVOICE_URL;


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

        paymentVerifyResponse.setStatus("FAILED");   /* this set to FAILED when the if condition above is false */
        paymentVerifyResponse.setUserId(userId);
        paymentVerifyResponse.setAmountPaid(0);

        return new ResponseEntity<>(paymentVerifyResponse,HttpStatus.NOT_FOUND);
    }

    private VerifyPaymentResponseDto verifyTransactionId(String transactionId, HttpHeader httpHeader) {

        String verifyUrl = baseUrl + transactionVerifyUrl + "{id}";

        HttpEntity<String> verifyRequest = new HttpEntity<>(httpHeader.getHeaders());

        ResponseEntity<String> verifyResponse = restTemplate.exchange(verifyUrl, HttpMethod.GET, verifyRequest, String.class, transactionId);

        if (verifyResponse.getStatusCode().value() == 200) {
            VerifyPaymentResponseDto verifyPaymentResponseDto = jsonObjectMapper.readValue(verifyResponse.getBody(), VerifyPaymentResponseDto.class);

            log.info("transaction Verification: " + verifyPaymentResponseDto.toString());

            return verifyPaymentResponseDto;
        } else {
            throw new NotFoundException("error verifying payment with transaction id");
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
        String updateUrl = baseUrl + updateInvoiceUrl + "{involceId}";

        ResponseEntity<String> invoiceResponse = restTemplate
                .exchange(updateUrl, HttpMethod.GET, updateInvoiceRequest, String.class, invoiceId);

        if (invoiceResponse.getStatusCode().value() != 200) {
            throw new NotFoundException("error");
        }

        CheckInvoiceResponseDto invoiceResponseDto =
                jsonObjectMapper.readValue(invoiceResponse.getBody(), CheckInvoiceResponseDto.class);

        return invoiceResponseDto;
    }

}




