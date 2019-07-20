package com.binance.dto;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.binance.api.OrderAPI;
import com.binance.helper.RestTemplateHelper;
import com.binance.model.Order;
import com.binance.model.WinningCoin;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

/**
 * OrderDTO
 */
@Component
public class OrderDTO {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderDTO.class);

    @Autowired
    private OrderAPI orderAPI;

    @Autowired
    private RestTemplateHelper restTemplateHelper;

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

    public Order postBuyOrder(WinningCoin winningCoin, Double quantity)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        LOGGER.info("Placing buy order for " + winningCoin.getSymbol() + "...");

        long timeStamp = orderAPI.getTimestamp();

        String signature = "symbol=" + winningCoin.getSymbol() + "&side=" + side.BUY + "&type=" + type.MARKET
                + "&quantity=" + quantity + "&recvWindow=" + orderAPI.getRecvWindow() + "&timestamp=" + timeStamp;
        signature = orderAPI.getHmac256Signature(signature);

        String queryString = "?symbol=" + winningCoin.getSymbol() + "&side=" + side.BUY + "&type=" + type.MARKET
                + "&quantity=" + quantity + "&recvWindow=" + orderAPI.getRecvWindow() + "&timestamp=" + timeStamp
                + "&signature=" + signature;

        LOGGER.info(orderAPI.getORDER_ENDPOINT() + queryString);

        ResponseEntity<String> responseEntity = null;

        responseEntity = restTemplateHelper.postResponseEntitySHA256String(orderAPI.getORDER_ENDPOINT() + queryString,
                orderAPI.getApiKey());

        ObjectMapper objectMapper = new ObjectMapper();

        Order order = new Order();

        order = objectMapper.readValue(responseEntity.getBody(), Order.class);
        LOGGER.info(order.toString());

        return order;
    }

    public Order postSellOrder(WinningCoin winningCoin, Double quantity)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        LOGGER.info("Placing sell order for " + winningCoin.getSymbol() + "...");

        long timeStamp = orderAPI.getTimestamp();

        String signature = "symbol=" + winningCoin.getSymbol() + "&side=" + side.SELL + "&type=" + type.MARKET
                + "&quantity=" + quantity + "&recvWindow=" + orderAPI.getRecvWindow() + "&timestamp=" + timeStamp;
        signature = orderAPI.getHmac256Signature(signature);

        String queryString = "?symbol=" + winningCoin.getSymbol() + "&side=" + side.SELL + "&type=" + type.MARKET
                + "&quantity=" + quantity + "&recvWindow=" + orderAPI.getRecvWindow() + "&timestamp=" + timeStamp
                + "&signature=" + signature;

        LOGGER.info(orderAPI.getORDER_ENDPOINT() + queryString);

        ResponseEntity<String> responseEntity = null;

        responseEntity = restTemplateHelper.postResponseEntitySHA256String(orderAPI.getORDER_ENDPOINT() + queryString,
                orderAPI.getApiKey());

        ObjectMapper objectMapper = new ObjectMapper();

        Order order = new Order();

        order = objectMapper.readValue(responseEntity.getBody(), Order.class);
        LOGGER.info(order.toString());

        return order;
    }
}