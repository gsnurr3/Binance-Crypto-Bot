package com.binance.model;

import java.util.List;

/**
 * PotentialWinningCoin
 */
public class PotentialWinningCoin extends WinningCoin {

    String message;

    public PotentialWinningCoin() {

    }

    public PotentialWinningCoin(String symbol, String status, List<Double> prices, List<CandleStick_1H> candleSticks_1H,
            List<CandleStick_24H> candleSticks_24H) {
        super(symbol, status, prices, candleSticks_1H, candleSticks_24H);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "PotentialWinningCoin [message=" + message + "]";
    }
}