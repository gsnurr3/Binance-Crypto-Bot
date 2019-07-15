package com.binance.model;

import java.util.List;

/**
 * PotentialWinningCoin
 */
public class PotentialWinningCoin extends WinningCoin {

    Boolean isLowestPrice = false;
    Boolean isHighestPrice = false;

    String message;

    public PotentialWinningCoin() {

    }

    public PotentialWinningCoin(String symbol, String status, List<Double> prices, List<CandleStick_1H> candleSticks_1H,
            List<CandleStick_24H> candleSticks_24H) {
        super(symbol, status, prices, candleSticks_1H, candleSticks_24H);
    }

    public Boolean isLowestPrice() {
        return isLowestPrice;
    }

    public void setIsLowestPrice(Boolean isLowestPrice) {
        this.isLowestPrice = isLowestPrice;
    }

    public Boolean isHighestPrice() {
        return isHighestPrice;
    }

    public void setIsHighestPrice(Boolean isHighestPrice) {
        this.isHighestPrice = isHighestPrice;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PotentialWinningCoin [isHighestPrice=" + isHighestPrice + ", isLowestPrice=" + isLowestPrice
                + ", message=" + message + "]";
    }
}