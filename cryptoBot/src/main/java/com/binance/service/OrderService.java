package com.binance.service;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.binance.dto.OrderDTO;
import com.binance.handler.EmailHandler;
import com.binance.model.Order;
import com.binance.model.WinningCoin;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

/**
 * OrderService
 */
@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KlinesService.class);

    @Autowired
    private OrderDTO orderDTO;

    @Autowired
    private EmailHandler emailHandler;

    public Order postBuyOrder(WinningCoin winningCoin, Double quantity) throws ResourceAccessException,
            SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        return orderDTO.postBuyOrder(winningCoin, quantity);
    }

    public Order postSellOrder(WinningCoin winningCoin, Double quantity) {

        Order order = new Order();

        try {
            order = orderDTO.postSellOrder(winningCoin, quantity);
        } catch (ResourceAccessException | SocketTimeoutException | ConnectTimeoutException | NullPointerException e1) {

            LOGGER.error(e1.toString());
            emailHandler.sendEmail("Sell Error", e1.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            this.postSellOrder(winningCoin, quantity);
        } catch (IOException e3) {
            LOGGER.error(e3.toString());
            emailHandler.sendEmail("Sell Error", e3.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e4) {
                e4.printStackTrace();
            }
            this.postSellOrder(winningCoin, quantity);
        }

        return order;
    }
}