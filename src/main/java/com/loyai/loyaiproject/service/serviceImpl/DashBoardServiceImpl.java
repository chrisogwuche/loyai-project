package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.response.DashboardResponseDto;
import com.loyai.loyaiproject.dto.response.wallet.WalletData;
import com.loyai.loyaiproject.dto.response.wallet.WalletResponseDto;
import com.loyai.loyaiproject.exception.NotFoundException;
import com.loyai.loyaiproject.kodobe.HttpHeader;
import com.loyai.loyaiproject.repository.AirtimeRepository;
import com.loyai.loyaiproject.repository.UsersRepository;
import com.loyai.loyaiproject.repository.WinsRepository;
import com.loyai.loyaiproject.service.DashboardService;
import jakarta.annotation.PostConstruct;
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
public class DashBoardServiceImpl implements DashboardService {

    private final UsersRepository usersRepository;
    private final AirtimeRepository airtimeRepository;
    private final WinsRepository winsRepository;
    private final RestTemplate restTemplate;
    private final JsonObjectMapper jsonObjectMapper;

    @Value("${client_id}")
    private String clientId;
    @Value("${client_secret}")
    private String clientSecret;
    @Value("${denomination}")
    private String denomination;
    @Value("${gameInstanceId}")
    private String gameInstanceId;
    @Value("${baseUrl}")
    private String baseUrl;
    @Value("${clientBackgroundImage}")
    private String clientBackgroundImage;
    @Value("${gameUrl}")
    private String gameUrl;
    @Value("${clientLedgerId}")
    private String clientLedgerId;


    @Override
    public ResponseEntity<DashboardResponseDto> getDashboardData(String bearerToken) {




        WalletResponseDto walletResponseDto = getUserWallet(bearerToken);
        int userChances = walletResponseDto.get_embedded().getWallets().get(0).getBalance();
        String userId = walletResponseDto.get_embedded().getWallets().get(0).getUserId();
        String gamePlayUrl = gameUrl+"/?clientBackgroundImage="+clientBackgroundImage+"&baseUrl="+baseUrl+"/&gameInstanceId="+gameInstanceId+"&token="+bearerToken+"&userId="+userId+"&denomination="+denomination+"&clientId="+clientId;
        int amountToBeWon = amountToBeWon();

        DashboardResponseDto dashboardResponseDto = new DashboardResponseDto();

        dashboardResponseDto.setRewardPool(amountToBeWon);
        dashboardResponseDto.setUserChances(userChances);
        dashboardResponseDto.setGamePlayUrl(gamePlayUrl);

        log.info("dashboard_response_dto: "+dashboardResponseDto.toString());

        return ResponseEntity.ok(dashboardResponseDto);
    }

    private int amountToBeWon() {
        Long sumAmountWon = winsRepository.sumAmountWon();
        Long sumAirtimeBought = usersRepository.sumAirtimeBought();

        log.info("sum of amount won: " +sumAmountWon);
        log.info("sum of airtime bought: "+sumAirtimeBought);

        if(sumAmountWon == null){
            sumAmountWon = 0L;
        }
        if(sumAirtimeBought == null){
            sumAirtimeBought = 0L;
        }

        int amountToBeWon = (int) (sumAirtimeBought - sumAmountWon);

        log.info("amount to be won: "+amountToBeWon);

        return  amountToBeWon;
    }

    private WalletResponseDto getUserWallet(String token){

        HttpHeader header = new HttpHeader(clientId,clientSecret,token);
        String url = baseUrl+"/billing/wallets?clientLedgerId="+clientLedgerId;

        HttpEntity<String> walletRequest = new HttpEntity<>(header.getHeaders());

        ResponseEntity<String> walletResponse = restTemplate.exchange(url, HttpMethod.GET,walletRequest,String.class);

        log.info("wallet response: "+walletResponse.getBody().toString());

        if(walletResponse.getStatusCode().value()==200){

            WalletResponseDto walletResponseDto = jsonObjectMapper.readValue(walletResponse.getBody(),WalletResponseDto.class);

            log.info("walletResponseDto: " +walletResponseDto.toString());
            return walletResponseDto;
        }
        throw  new NotFoundException(walletResponse.getBody());

    }

}
