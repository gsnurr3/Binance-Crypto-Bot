package com.binance.dto;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import com.binance.api.PriceAPI;
import com.binance.helper.RestTemplateHelper;
import com.binance.model.Coin;
import com.binance.model.Price;
import com.binance.model.WinningCoin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

/**
 * PriceDTO
 */
@Component
public class PriceDTO {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceDTO.class);

    @Autowired
    private PriceAPI priceApi;

    @Autowired
    private RestTemplateHelper restTemplateHelper;

    public List<Coin> getAllPrices(List<Coin> coins)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        ResponseEntity<String> responseEntity = null;

        responseEntity = restTemplateHelper.getResponseEntityString(priceApi.getPRICE_ENDPOINT());

        ObjectMapper objectMapper = new ObjectMapper();

        Price[] prices = null;

        prices = objectMapper.readValue(responseEntity.getBody(), Price[].class);

        if (coins.size() > 0 && prices.length > 0) {
            for (Price price : prices) {
                for (Coin coin : coins) {
                    if (price.getSymbol().equals(coin.getSymbol())) {
                        coin.addPrice(price.getPrice());
                    }
                }
            }
        }

        return coins;
    }

    public WinningCoin getPrice(WinningCoin winningCoin)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        String queryString = "?symbol=" + winningCoin.getSymbol();

        ResponseEntity<String> responseEntity = null;

        responseEntity = restTemplateHelper.getResponseEntityString(priceApi.getPRICE_ENDPOINT() + queryString);

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = null;

        jsonNode = objectMapper.readTree(responseEntity.getBody());

        if (winningCoin.getSymbol().equals(jsonNode.get("symbol").asText())) {
            winningCoin.setCurrentPrice(jsonNode.get("price").asDouble());
            winningCoin.addPrice(winningCoin.getCurrentPrice());
        } else {
            LOGGER.info("ERROR: Updated price for symbol does not match winning coin symbol...");
        }

        return winningCoin;
    }

    public WinningCoin getUSDTPrice(WinningCoin winningCoin)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        String queryString = "?symbol=" + winningCoin.getSymbol();
        queryString = queryString.replaceAll("BTC\\b", "USDT");

        ResponseEntity<String> responseEntity = null;

        responseEntity = restTemplateHelper.getResponseEntityString(priceApi.getPRICE_ENDPOINT() + queryString);

        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode jsonNode = null;

        jsonNode = objectMapper.readTree(responseEntity.getBody());

        WinningCoin tempCoin = new WinningCoin();

        if (winningCoin.getSymbol().equals(jsonNode.get("symbol").asText().replaceAll("USDT\\b", "BTC"))) {
            tempCoin.setCurrentPrice(jsonNode.get("price").asDouble());
            winningCoin.setUsdtPrice(tempCoin.getCurrentPrice());
        } else {
            LOGGER.info("ERROR: Updated price for symbol does not match winning coin symbol...");
        }

        return winningCoin;
    }
}