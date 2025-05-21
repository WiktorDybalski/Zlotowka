package com.agh.zlotowka.dto;

import com.agh.zlotowka.model.Currency;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
public class TransactionDTO {
    private Integer transactionId;
    private Integer userId;
    private String name;
    private BigDecimal amount;
    private Currency currency;
    private Boolean isIncome;
    private LocalDate date;
    private String description;
    private PeriodDTO period;
}
