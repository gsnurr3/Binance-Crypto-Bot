package com.binance.dto;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import com.binance.api.ExchangeInfoAPI;
import com.binance.handler.EmailHandler;
import com.binance.helper.RestTemplateHelper;
import com.binance.model.Coin;
import com.binance.model.ExchangeInfo;
import com.binance.model.Symbols;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

/**
 * ExchangeInfo
 */
@Component
public class ExchangeInfoDTO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceDTO.class);

    @Autowired
    private ExchangeInfoAPI exchangeInfoAPI;

    @Autowired
    private RestTemplateHelper restTemplateHelper;

    @Autowired
    private EmailHandler emailhandler;

    public List<Coin> getExchangeInfo(List<Coin> coins) {

        LOGGER.info("Initializing coins for Binance BTC Market...");

        ResponseEntity<String> responseEntity = null;

        try {
            responseEntity = restTemplateHelper.getResponseEntityString(exchangeInfoAPI.getEXCHANGE_INFO_ENDPOINT());
        } catch (ResourceAccessException | SocketTimeoutException e) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e1) {
                LOGGER.error(e.getMessage(), e);
            }
            getExchangeInfo(coins);
        }

        ObjectMapper objectMapper = new ObjectMapper();

        ExchangeInfo exchangeInfo = null;

        try {
            exchangeInfo = objectMapper.readValue(responseEntity.getBody(), ExchangeInfo.class);
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            emailhandler.sendEmail("Error", e.toString());
        }

        int index = 0;

        if (coins.size() == 0) {
            for (Symbols symbol : exchangeInfo.getSymbols()) {
                if (symbol.getSymbol().matches(".*BTC\\b") && !symbol.getSymbol().contains("US")
                        && !symbol.getSymbol().contains("BNB") && symbol.getStatus().equals("TRADING")) {
                    Coin coin = new Coin();
                    coin.setSymbol(symbol.getSymbol());
                    coin.setStatus(symbol.getStatus());

                    coins.add(coin);

                    index++;
                }
            }
        }

        LOGGER.info("Found " + index + " coins currently trading on the Binance BTC Market...");

        return coins;
    }
}