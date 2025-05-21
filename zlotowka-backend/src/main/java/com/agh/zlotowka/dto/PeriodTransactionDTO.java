package com.agh.zlotowka.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Setter
@Getter
@SuperBuilder
public class PeriodTransactionDTO extends TransactionDTO {
    private LocalDate firstPaymentDate;
    private LocalDate nextPaymentDate;
    private LocalDate finalPaymentDate;
}
