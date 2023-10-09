package com.loyai.loyaiproject.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "t_wins")
public class Wins {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private Integer amountWon;
    @CreatedDate
    private LocalDateTime localDateTime;
    private String transactionRef;
    private String gameInstanceId;
    private String prizeId;
    private String deliveryId;
    private String userId;
}
