package com.binance.service;

import com.binance.dto.AccountDTO;
import com.binance.model.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * AccountService
 */
@Service
public class AccountService {

    @Autowired
    private AccountDTO accountDTO;

    public Account getAccountInfo() {

        return accountDTO.getAccountInfo();
    }
}