package com.loyai.loyaiproject.service.serviceImpl;

import com.loyai.loyaiproject.dto.request.WebhookRequestDto;
import com.loyai.loyaiproject.model.Users;
import com.loyai.loyaiproject.model.Wins;
import com.loyai.loyaiproject.repository.UsersRepository;
import com.loyai.loyaiproject.repository.WinsRepository;
import com.loyai.loyaiproject.service.WebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebhookServiceImpl implements WebhookService {
    private final WinsRepository winsRepository;
    private final UsersRepository usersRepository;


    @Override
    public ResponseEntity<WebhookRequestDto> setWebhookData(WebhookRequestDto webhookRequestDto) {
        String event = webhookRequestDto.getEvent();
        String webhookStatus = webhookRequestDto.getData().getStatus();

        if(event.equals("game.updated.won") && webhookStatus.equals("WON")){
            String userId = webhookRequestDto.getData().getUserId();

            Optional<Users> user = usersRepository.findByUserId(userId);
            log.info("user won detail: "+user.get().toString());

            saveWinner(webhookRequestDto,user.get());
        }

        return new ResponseEntity<>(webhookRequestDto, HttpStatus.OK);
    }

    private void saveWinner(WebhookRequestDto webhookRequestDto,Users user){

        Integer amountWon = Integer.valueOf(webhookRequestDto.getData().getPrizeAmount());
        String transactionRef = webhookRequestDto.getData().getTransactionRef();
        String gameInstanceId = webhookRequestDto.getData().getGameInstanceId();
        String prizeId = webhookRequestDto.getData().getPrizeId();
        String deliveryId =webhookRequestDto.getDeliveryId();

        Wins winnerInfo = new Wins();
        winnerInfo.setAmountWon(amountWon);
        winnerInfo.setDeliveryId(deliveryId);
        winnerInfo.setTransactionRef(transactionRef);
        winnerInfo.setLocalDateTime(LocalDateTime.now());
        winnerInfo.setPrizeId(prizeId);
        winnerInfo.setGameInstanceId(gameInstanceId);
        winnerInfo.setUser(user);

        log.info("saving winner in database" +winnerInfo.toString());

        winsRepository.save(winnerInfo);
    }
}
