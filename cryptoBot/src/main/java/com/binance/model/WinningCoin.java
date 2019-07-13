package com.binance.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * WinningCoin
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class WinningCoin extends Coin {

    @JsonProperty("price")
    private Double currentPrice;

    private Double usdtPrice;
    private Double buyPrice;
    private Double sellPrice;
    private Double highestPrice;
    private Double marginFromCurrentAndHighestPrice;
    private Double profit;
    private Double profitSinceBuyPrice;
    private Boolean isBought = false;
    private Boolean isSold = false;

    public WinningCoin() {

    }

    public WinningCoin(String symbol, String status, List<Double> prices, List<CandleStick_1H> candleSticks_1H, List<CandleStick_24H> candleSticks_24H) {
        super(symbol, status, prices, candleSticks_1H, candleSticks_24H);
    }

    public Double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(Double currentPrice) {
        this.currentPrice = currentPrice;

        if (this.highestPrice == null) {
            this.highestPrice = currentPrice;
        }
        if (currentPrice > this.highestPrice) {
            this.highestPrice = currentPrice;
        }
    }

    public Double getUsdtPrice() {
        return usdtPrice;
    }

    public void setUsdtPrice(Double usdtPrice) {
        this.usdtPrice = usdtPrice;
    }

    public Double getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(Double buyPrice) {
        this.buyPrice = buyPrice;
        this.setHighestPrice(buyPrice);
    }

    public Double getSellPrice() {
        return sellPrice;
    }

    public void setSellPrice(Double sellPrice) {
        this.sellPrice = sellPrice;
    }

    public Double getHighestPrice() {
        return highestPrice;
    }

    public void setHighestPrice(Double highestPrice) {
        this.highestPrice = highestPrice;
    }

    public Double getMarginFromCurrentAndHighestPrice() {
        return marginFromCurrentAndHighestPrice;
    }

    public void setMarginFromCurrentAndHighestPrice(Double currentPrice, Double highestPrice) {
        this.marginFromCurrentAndHighestPrice = ((currentPrice - highestPrice) / highestPrice) * 100;
    }

    public Double getProfit() {
        return profit;
    }

    public void setProfit() {
        this.profit = ((this.getSellPrice() - this.getBuyPrice()) / this.getBuyPrice()) * 100;
    }

    public Double getProfitSinceBuyPrice() {
        return this.profitSinceBuyPrice;
    }

    public void setProfitSinceBuyPrice() {
        this.profitSinceBuyPrice = ((this.currentPrice - this.getBuyPrice()) / this.getBuyPrice()) * 100;
    }

    public Boolean isBought() {
        return isBought;
    }

    public void setBought(Boolean isBought) {
        this.isBought = isBought;
    }

    public Boolean isSold() {
        return isSold;
    }

    public void setSold(Boolean isSold) {
        this.isSold = isSold;
    }

    @Override
    public String toString() {
        return "WinningCoin [buyPrice=" + buyPrice + ", currentPrice=" + currentPrice + ", highestPrice=" + highestPrice
                + ", isBought=" + isBought + ", isSold=" + isSold + ", marginFromCurrentAndHighestPrice="
                + marginFromCurrentAndHighestPrice + ", profit=" + profit + ", profitSinceBuyPrice="
                + profitSinceBuyPrice + ", sellPrice=" + sellPrice + ", usdtPrice=" + usdtPrice + "]";
    }
}