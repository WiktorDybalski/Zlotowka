package com.agh.zlotowka.dto;

import java.util.List;

public record PaginatedTransactionsDTO(
    List<TransactionDTO> transactions,
    int total,
    int page,
    int totalPages
) {

}
