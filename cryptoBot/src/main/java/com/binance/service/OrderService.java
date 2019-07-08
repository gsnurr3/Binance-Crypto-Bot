package com.binance.service;

import com.binance.dto.OrderDTO;
import com.binance.model.Order;
import com.binance.model.WinningCoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrderService
 */
@Service
public class OrderService {

    @Autowired
    private OrderDTO orderDTO;

    public Order postBuyOrder(WinningCoin winningCoin, Double quantity)  {

        return orderDTO.postBuyOrder(winningCoin, quantity);
    }

    public Order postSellOrder(WinningCoin winningCoin, Double quantity)  {

        return orderDTO.postSellOrder(winningCoin, quantity);
    }
}