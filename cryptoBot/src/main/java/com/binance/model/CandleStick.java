package com.binance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * CandleStick_24H
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandleStick {

    private Long openTime;
    private Double openPrice;
    private Double highPrice;
    private Double lowPrice;
    private Double closePrice;
    private Double volume;
    private Long closeTime;
    private Double quoteAssetVolume;
    private Double numberOfTrades;
    private Double takerBuyBaseAssetVolume;
    private Double takerBuyQuoteAssetVolume;
    private Double ignore;
    private Double endOfCandleStickGain = 0.0;
    private Double endOfCandleStickLoss = 0.0;

    public Long getOpenTime() {
        return openTime;
    }

    public void setOpenTime(Long openTime) {
        this.openTime = openTime;
    }

    public Double getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(Double openPrice) {
        this.openPrice = openPrice;
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Double getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(Double lowPrice) {
        this.lowPrice = lowPrice;
    }

    public Double getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(Double closePrice) {
        this.closePrice = closePrice;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Long getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(Long closeTime) {
        this.closeTime = closeTime;
    }

    public Double getQuoteAssetVolume() {
        return quoteAssetVolume;
    }

    public void setQuoteAssetVolume(Double quoteAssetVolume) {
        this.quoteAssetVolume = quoteAssetVolume;
    }

    public Double getNumberOfTrades() {
        return numberOfTrades;
    }

    public void setNumberOfTrades(Double numberOfTrades) {
        this.numberOfTrades = numberOfTrades;
    }

    public Double getTakerBuyBaseAssetVolume() {
        return takerBuyBaseAssetVolume;
    }

    public void setTakerBuyBaseAssetVolume(Double takerBuyBaseAssetVolume) {
        this.takerBuyBaseAssetVolume = takerBuyBaseAssetVolume;
    }

    public Double getTakerBuyQuoteAssetVolume() {
        return takerBuyQuoteAssetVolume;
    }

    public void setTakerBuyQuoteAssetVolume(Double takerBuyQuoteAssetVolume) {
        this.takerBuyQuoteAssetVolume = takerBuyQuoteAssetVolume;
    }

    public Double getIgnore() {
        return ignore;
    }

    public void setIgnore(Double ignore) {
        this.ignore = ignore;
    }

    public Double getEndOfCandleStickGain() {
        return endOfCandleStickGain;
    }

    public void setEndOfCandleStickGain(Double endOfCandleStickGain) {
        this.endOfCandleStickGain = endOfCandleStickGain;
    }

    public Double getEndOfCandleStickLoss() {
        return endOfCandleStickLoss;
    }

    public void setEndOfCandleStickLoss(Double endOfCandleStickLoss) {
        this.endOfCandleStickLoss = endOfCandleStickLoss;
    }

    @Override
    public String toString() {
        return "CandleStick [closePrice=" + closePrice + ", closeTime=" + closeTime + ", endOfCandleStickGain="
                + endOfCandleStickGain + ", endOfCandleStickLoss=" + endOfCandleStickLoss + ", highPrice=" + highPrice
                + ", ignore=" + ignore + ", lowPrice=" + lowPrice + ", numberOfTrades=" + numberOfTrades
                + ", openPrice=" + openPrice + ", openTime=" + openTime + ", quoteAssetVolume=" + quoteAssetVolume
                + ", takerBuyBaseAssetVolume=" + takerBuyBaseAssetVolume + ", takerBuyQuoteAssetVolume="
                + takerBuyQuoteAssetVolume + ", volume=" + volume + "]";
    }
}