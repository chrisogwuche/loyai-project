package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.request.AddMoneyToWalletRequest;
import com.loyai.loyaiproject.dto.response.payment.PaymentVerifyResponse;
import com.loyai.loyaiproject.dto.response.invoice.CheckInvoiceResponseDto;
import com.loyai.loyaiproject.dto.response.payment.VerifyPaymentResponseDto;
import com.loyai.loyaiproject.exception.NotFoundException;
import com.loyai.loyaiproject.kodobe.HttpHeader;
import com.loyai.loyaiproject.kodobe.KodobeURLs;
import com.loyai.loyaiproject.model.Users;
import com.loyai.loyaiproject.repository.UsersRepository;
import com.loyai.loyaiproject.service.PaymentService;
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
    private final String paymentServiceUrl = KodobeURLs.PAYMENT_SERVICE_URL;
    private final String invoiceServiceUrl = KodobeURLs.INVOICE_SERVICE_URL;
    private final UsersRepository usersRepository;
    private final String addAmountToWalletUrl = KodobeURLs.ADD_AMOUNT_TO_WALLET_URL;



    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${FT_applicationId}")
    private String FTapplicationId;
    @Value("${clientLedgerId}")
    private String clientLedgerId;
    @Value("${baseUrl}")
    private String baseUrl;


    @Override
    public ResponseEntity<PaymentVerifyResponse> verifyPayment(String transactionRef, String userId) {

        log.info("transac Id:-----" + transactionRef);

        return paymentVerification(transactionRef,userId);
    }

    private ResponseEntity<PaymentVerifyResponse> paymentVerification(String transactionRef, String userId) {
        HttpHeader httpHeader = new HttpHeader(clientId, clientSecret);

        VerifyPaymentResponseDto verifyPaymentResponseDto = verifyTransactionId(transactionRef, httpHeader);

        String invoiceId = verifyPaymentResponseDto.getData().getInvoiceId();
        int realAmountPaid = (verifyPaymentResponseDto.getData().getAmount()) / 100;

        CheckInvoiceResponseDto checkInvoiceResponseDto = getInvoice(invoiceId, httpHeader);

        boolean isAmountVerified = verifyAmountPaidAndInvoiceStatus(checkInvoiceResponseDto, realAmountPaid);
        boolean isIdVerified = checkInvoiceResponseDto.getData().getUserId().equals(userId);
        String transactionStatus = verifyPaymentResponseDto.getData().getStatus();
        int amountPaid = checkInvoiceResponseDto.getData().getAmount();

        PaymentVerifyResponse paymentVerifyResponse = new PaymentVerifyResponse();

        if (isAmountVerified && transactionStatus.equals("SUCCESS") && isIdVerified) {
            paymentVerifyResponse.setStatus("SUCCESS");

            saveUserToDatabase(userId,amountPaid,transactionRef);  /* saves the user and the airtime amount bought in the database*/

            int noOfChances = realAmountPaid/100;
            addMoneyToWallet(noOfChances,userId,httpHeader);

            return new ResponseEntity<>(paymentVerifyResponse,HttpStatus.OK);
        }

        throw new NotFoundException("No transaction found for this user");
    }

    private VerifyPaymentResponseDto verifyTransactionId(String transactionRef, HttpHeader httpHeader) {

        String verifyUrl = paymentServiceUrl+"/v1/flutterwave/verify/"+"{id}";

        HttpEntity<String> verifyRequest = new HttpEntity<>(httpHeader.getHeaders());

        ResponseEntity<String> verifyResponse = restTemplate.exchange(verifyUrl, HttpMethod.GET, verifyRequest, String.class, transactionRef);

        if (verifyResponse.getStatusCode().value() == 200) {
            VerifyPaymentResponseDto verifyPaymentResponseDto = jsonObjectMapper.readValue(verifyResponse.getBody(), VerifyPaymentResponseDto.class);

            log.info("transaction Verification: " + verifyPaymentResponseDto.toString());

            return verifyPaymentResponseDto;
        } else {
            throw new NotFoundException("error verifying payment with tx_ref");
        }
    }

    private boolean verifyAmountPaidAndInvoiceStatus(CheckInvoiceResponseDto invoiceResponseDto,int amount) {

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

    private void saveUserToDatabase(String userId,int amountPaid,String transactionRef){

        Users user = new Users();
        user.setUserId(userId);
        user.setAirtimeBought(amountPaid);
        user.setTransaction_ref(transactionRef);
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

        String addToWalletUrl = baseUrl+addAmountToWalletUrl+"/transfer";

        ResponseEntity<String> walletResponse = restTemplate.exchange(addToWalletUrl,HttpMethod.POST,addRequest,String.class);

        log.info("wallet response: " +walletResponse.getBody());
    }

}
