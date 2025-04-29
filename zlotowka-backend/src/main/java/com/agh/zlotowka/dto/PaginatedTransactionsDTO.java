package com.agh.zlotowka.dto;

import java.util.List;

public record PaginatedTransactionsDTO(
    List<OneTimeTransactionDTO> transactions,
    int total,
    int page,
    int totalPages
) {

}
