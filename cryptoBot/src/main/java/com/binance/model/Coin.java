package com.binance.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * CoinModel
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Coin {

    private String symbol;

    private String status;

    private List<Double> prices = new ArrayList<>();

    private List<CandleStick_24H> candleSticks_24H = new ArrayList<>();

    public Coin() {

    }

    public Coin(String symbol, String status, List<Double> prices, List<CandleStick_24H> candleSticks_24H) {
        this.symbol = symbol;
        this.status = status;
        this.prices = prices;
        this.candleSticks_24H = candleSticks_24H;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public List<Double> getPrices() {
        return prices;
    }

    public void addPrice(Double price) {

        if (prices.size() > 0) {
            if (price != prices.get(prices.size() - 1)) {
                if (prices.size() == 2) {
                    this.prices.remove(0);
                }
                this.prices.add(price);
            }  
        } else {
            this.prices.add(price);
        }
    }

    public List<CandleStick_24H> getCandleSticks_24H() {
        return candleSticks_24H;
    }

    public void addCandleSticks_24H(CandleStick_24H candleStick_24H) {
        this.candleSticks_24H.add(candleStick_24H);
    }

    @Override
    public String toString() {
        return "Coin [candleSticks_24H=" + candleSticks_24H + ", prices=" + prices + ", symbol=" + symbol + "]";
    }
}