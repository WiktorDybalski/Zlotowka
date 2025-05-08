package com.agh.zlotowka.dto;

import java.math.BigDecimal;

public record RevenuesAndExpensesResponse(
        BigDecimal revenues,
        BigDecimal expenses,
        String currencyIsoCode
) {
}
