package com.binance.service;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.binance.dto.AccountDTO;
import com.binance.model.Account;

import org.apache.http.conn.ConnectTimeoutException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

/**
 * AccountService
 */
@Service
public class AccountService {

    @Autowired
    private AccountDTO accountDTO;

    public Account getAccountInfo() throws ResourceAccessException, SocketTimeoutException, IOException,
            NullPointerException, ConnectTimeoutException {

        return accountDTO.getAccountInfo();
    }
}