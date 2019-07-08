package com.binance.service;

import java.util.List;

import com.binance.dto.PriceDTO;
import com.binance.model.Coin;
import com.binance.model.WinningCoin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PriceService
 */
@Service
public class PriceService {

    @Autowired
    private PriceDTO priceDTO;

    public List<Coin> getAllPrices(List<Coin> coins) {

        return priceDTO.getAllPrices(coins);
    }

    public WinningCoin getPrice(WinningCoin winningCoin) {

        winningCoin = priceDTO.getPrice(winningCoin);

        winningCoin.setMarginFromCurrentAndHighestPrice(winningCoin.getCurrentPrice(), winningCoin.getHighestPrice());

        return winningCoin;
    }

    public WinningCoin getUSDTPrice(WinningCoin winningCoin) {

        return priceDTO.getUSDTPrice(winningCoin);
    }
}