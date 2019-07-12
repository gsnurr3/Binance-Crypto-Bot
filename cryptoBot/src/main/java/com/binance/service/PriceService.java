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

    // private static final Logger LOGGER =
    // LoggerFactory.getLogger(PriceService.class);

    @Autowired
    private PriceDTO priceDTO;

    public List<Coin> getAllPrices(List<Coin> coins) {

        return priceDTO.getAllPrices(coins);
    }

    public WinningCoin updateWinningCoinPrice(List<Coin> coins, WinningCoin winningCoin) {

        // LOGGER.info("Updating winning coin's current price...");

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

        winningCoin = priceDTO.getPrice(winningCoin);

        winningCoin.setMarginFromCurrentAndHighestPrice(winningCoin.getCurrentPrice(), winningCoin.getHighestPrice());

        return winningCoin;
    }

    public WinningCoin getUSDTPrice(WinningCoin winningCoin) {

        return priceDTO.getUSDTPrice(winningCoin);
    }
}