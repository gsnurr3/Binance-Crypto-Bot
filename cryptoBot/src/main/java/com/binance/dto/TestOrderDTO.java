package com.binance.dto;

import com.binance.api.TestOrderAPI;
import com.binance.handler.EmailHandler;
import com.binance.helper.RestTemplateHelper;
import com.binance.model.WinningCoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

/**
 * TestOrderDTO
 */
@Component
public class TestOrderDTO {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestOrderAPI.class);

    @Autowired
    private TestOrderAPI testOrderAPI;

    @Autowired
    private RestTemplateHelper restTemplateHelper;

    @Autowired
    private EmailHandler emailhandler;

    enum side {
        BUY, SELL;
    }

    enum type {
        MARKET, LIMIT;
    }

    // GTC (Good-Til-Canceled) orders are effective until they are executed or
    // canceled.
    // IOC (Immediate or Cancel) orders fills all or part of an order immediately
    // and cancels the remaining part of the order.
    // FOK (Fill or Kill) orders fills all in its entirety, otherwise, the entire
    // order will be cancelled.
    enum timeInForce {
        GTC, IOC, FOK;
    }

    public void postBuyOrder(WinningCoin winningCoin, Double quantity) {

        LOGGER.info("Placing buy order for " + winningCoin.getSymbol() + "...");

        long timeStamp = testOrderAPI.getTimestamp();

        String signature = "symbol=" + winningCoin.getSymbol() + "&side=" + side.BUY + "&type=" + type.MARKET
                + "&quantity=" + quantity + "&recvWindow=" + testOrderAPI.getRecvWindow() + "&timestamp=" + timeStamp;
        signature = testOrderAPI.getHmac256Signature(signature);

        String queryString = "?symbol=" + winningCoin.getSymbol() + "&side=" + side.BUY + "&type=" + type.MARKET
                + "&quantity=" + quantity + "&recvWindow=" + testOrderAPI.getRecvWindow() + "&timestamp=" + timeStamp
                + "&signature=" + signature;

        LOGGER.info(testOrderAPI.getTEST_ORDER_ENDPOINT() + queryString);

        ResponseEntity<String> responseEntity = null;

        try {
            responseEntity = restTemplateHelper.postResponseEntitySHA256String(
                    testOrderAPI.getTEST_ORDER_ENDPOINT() + queryString, testOrderAPI.getApiKey());
            LOGGER.info("Response: " + responseEntity.getBody());
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            emailhandler.sendEmail("Error", e.toString());
        }
    }

    public void postSellOrder(WinningCoin winningCoin, Double quantity) {

        LOGGER.info("Placing sell order for " + winningCoin.getSymbol() + "...");

        long timeStamp = testOrderAPI.getTimestamp();

        String signature = "symbol=" + winningCoin.getSymbol() + "&side=" + side.SELL + "&type=" + type.MARKET
                + "&quantity=" + quantity + "&recvWindow=" + testOrderAPI.getRecvWindow() + "&timestamp=" + timeStamp;
        signature = testOrderAPI.getHmac256Signature(signature);

        String queryString = "?symbol=" + winningCoin.getSymbol() + "&side=" + side.SELL + "&type=" + type.MARKET
                + "&quantity=" + quantity + "&recvWindow=" + testOrderAPI.getRecvWindow() + "&timestamp=" + timeStamp
                + "&signature=" + signature;

        LOGGER.info(testOrderAPI.getTEST_ORDER_ENDPOINT() + queryString);

        ResponseEntity<String> responseEntity = null;

        try {
            responseEntity = restTemplateHelper.postResponseEntitySHA256String(
                    testOrderAPI.getTEST_ORDER_ENDPOINT() + queryString, testOrderAPI.getApiKey());
            LOGGER.info("Response: " + responseEntity.getBody());
        } catch (ResourceAccessException e) {
            LOGGER.error(e.getMessage(), e);
        } catch (Exception e) {
            emailhandler.sendEmail("Error", e.toString());
        }
    }
}