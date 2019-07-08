package com.binance.service;

import com.binance.dto.TestOrderDTO;
import com.binance.model.WinningCoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * TestOrderService
 */
@Service
public class TestOrderService {

    @Autowired
    private TestOrderDTO testOrderDTO;

    public void postBuyOrder(WinningCoin winningCoin, Double quantity) {

        testOrderDTO.postBuyOrder(winningCoin, quantity);
    }

    public void postSellOrder(WinningCoin winningCoin, Double quantity) {

        testOrderDTO.postSellOrder(winningCoin, quantity);
    }
}