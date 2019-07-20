package com.binance.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import com.binance.dto.PriceDTO;
import com.binance.handler.EmailHandler;
import com.binance.model.Coin;
import com.binance.model.WinningCoin;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

/**
 * PriceService
 */
@Service
public class PriceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceService.class);

    @Autowired
    private PriceDTO priceDTO;

    @Autowired
    private EmailHandler emailHandler;

    public List<Coin> getAllPrices(List<Coin> coins) {

        try {
            coins = priceDTO.getAllPrices(coins);
        } catch (ResourceAccessException | SocketTimeoutException | ConnectTimeoutException | NullPointerException e1) {

            LOGGER.error(e1.toString());
            emailHandler.sendEmail("Error", e1.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            this.getAllPrices(coins);
        } catch (IOException e3) {
            LOGGER.error(e3.toString());
            emailHandler.sendEmail("Error", e3.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e4) {
                e4.printStackTrace();
            }
            this.getAllPrices(coins);
        }

        return coins;
    }

    public WinningCoin updateWinningCoinPrice(List<Coin> coins, WinningCoin winningCoin) {

        for (Coin coin : coins) {
            if (coin.getSymbol().equals(winningCoin.getSymbol())) {
                winningCoin.setCurrentPrice(coin.getPrices().get(coin.getPrices().size() - 1));
                winningCoin.addPrice(winningCoin.getCurrentPrice());
                winningCoin.setMarginFromCurrentAndHighestPrice(winningCoin.getCurrentPrice(),
                        winningCoin.getHighestPrice());
            }
        }

        return winningCoin;
    }

    public WinningCoin getPrice(WinningCoin winningCoin) {

        try {
            winningCoin = priceDTO.getPrice(winningCoin);
        } catch (ResourceAccessException | SocketTimeoutException | ConnectTimeoutException | NullPointerException e1) {

            LOGGER.error(e1.toString());
            emailHandler.sendEmail("Error", e1.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            this.getPrice(winningCoin);
        } catch (IOException e3) {
            LOGGER.error(e3.toString());
            emailHandler.sendEmail("Error", e3.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e4) {
                e4.printStackTrace();
            }
            this.getPrice(winningCoin);
        }

        winningCoin.setMarginFromCurrentAndHighestPrice(winningCoin.getCurrentPrice(), winningCoin.getHighestPrice());

        return winningCoin;
    }

    public WinningCoin getUSDTPrice(WinningCoin winningCoin) {

        try {
            winningCoin = priceDTO.getUSDTPrice(winningCoin);
        } catch (ResourceAccessException | SocketTimeoutException | ConnectTimeoutException | NullPointerException e1) {

            LOGGER.error(e1.toString());
            emailHandler.sendEmail("Error", e1.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            this.getUSDTPrice(winningCoin);
        } catch (IOException e3) {
            LOGGER.error(e3.toString());
            emailHandler.sendEmail("Error", e3.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e4) {
                e4.printStackTrace();
            }
            this.getUSDTPrice(winningCoin);
        }

        return winningCoin;
    }
}