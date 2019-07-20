package com.binance.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import com.binance.dto.ExchangeInfoDTO;
import com.binance.handler.EmailHandler;
import com.binance.model.Coin;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

/**
 * ExchangeInfoService
 */
@Service
public class ExchangeInfoService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeInfoService.class);

    @Autowired
    private ExchangeInfoDTO exchangeInfoDTO;

    @Autowired
    private EmailHandler emailHandler;

    public List<Coin> getExchangeInfo(List<Coin> coins) {

        try {
            coins = exchangeInfoDTO.getExchangeInfo(coins);
        } catch (ResourceAccessException | SocketTimeoutException | ConnectTimeoutException | NullPointerException e1) {

            LOGGER.error(e1.toString());
            emailHandler.sendEmail("Error", e1.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            this.getExchangeInfo(coins);
        } catch (IOException e3) {
            LOGGER.error(e3.toString());
            emailHandler.sendEmail("Error", e3.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e4) {
                e4.printStackTrace();
            }
            this.getExchangeInfo(coins);
        }

        return coins;
    }
}