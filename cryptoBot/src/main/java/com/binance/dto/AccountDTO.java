package com.binance.dto;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.binance.api.AccountAPI;
import com.binance.helper.RestTemplateHelper;
import com.binance.model.Account;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

/**
 * AccountDTO
 */
@Component
public class AccountDTO {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountDTO.class);

    @Autowired
    private AccountAPI accountAPI;

    @Autowired
    private RestTemplateHelper restTemplateHelper;

    public Account getAccountInfo()
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        LOGGER.info("Retrieving account information...");

        long timeStamp = accountAPI.getTimestamp();

        String signature = "recvWindow=" + accountAPI.getRecvWindow() + "&timestamp=" + timeStamp;
        signature = accountAPI.getHmac256Signature(signature);

        String queryString = "?recvWindow=" + accountAPI.getRecvWindow() + "&timestamp=" + timeStamp + "&signature="
                + signature;

        ResponseEntity<String> responseEntity = null;

        responseEntity = restTemplateHelper
                .getResponseEntitySHA256String(accountAPI.getACCOUNT_ENDPOINT() + queryString, accountAPI.getApiKey());

        ObjectMapper objectMapper = new ObjectMapper();

        Account account = new Account();

        account = objectMapper.readValue(responseEntity.getBody(), Account.class);

        return account;
    }
}