package com.agh.zlotowka.service;

import com.agh.zlotowka.dto.OneTimeTransactionRequest;
import com.agh.zlotowka.model.OneTimeTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public void updateBudget(OneTimeTransaction transaction) {
    }

    public void updateBudget(OneTimeTransactionRequest request, OneTimeTransaction transaction) {
    }
}
