package com.binance.dto;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import com.binance.api.ExchangeInfoAPI;
import com.binance.helper.RestTemplateHelper;
import com.binance.model.Coin;
import com.binance.model.ExchangeInfo;
import com.binance.model.Symbols;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.conn.ConnectTimeoutException;
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

    public List<Coin> getExchangeInfo(List<Coin> coins)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        LOGGER.info("Initializing coins for Binance BTC Market...");

        ResponseEntity<String> responseEntity = null;

        responseEntity = restTemplateHelper.getResponseEntityString(exchangeInfoAPI.getEXCHANGE_INFO_ENDPOINT());

        ObjectMapper objectMapper = new ObjectMapper();

        ExchangeInfo exchangeInfo = null;

        exchangeInfo = objectMapper.readValue(responseEntity.getBody(), ExchangeInfo.class);

        int index = 0;

        coins.clear();
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