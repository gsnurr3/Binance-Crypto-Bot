package com.binance.service;

import java.io.IOException;
import java.net.SocketTimeoutException;

import com.binance.dto.OrderDTO;
import com.binance.model.Order;
import com.binance.model.WinningCoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

/**
 * OrderService
 */
@Service
public class OrderService {

    @Autowired
    private OrderDTO orderDTO;

    public Order postBuyOrder(WinningCoin winningCoin, Double quantity)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException {

        return orderDTO.postBuyOrder(winningCoin, quantity);
    }

    public Order postSellOrder(WinningCoin winningCoin, Double quantity)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException {

        return orderDTO.postSellOrder(winningCoin, quantity);
    }
}