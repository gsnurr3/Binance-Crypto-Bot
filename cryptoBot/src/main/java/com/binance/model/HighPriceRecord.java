package com.binance.model;

import java.util.Calendar;

/**
 * HighPriceRecord
 */
public class HighPriceRecord {

    private Double highPrice;
    private Calendar timeStamp;

    public HighPriceRecord(Double highPrice) {
        this.highPrice = highPrice;
        this.timeStamp = Calendar.getInstance();
    }

    public Double getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(Double highPrice) {
        this.highPrice = highPrice;
    }

    public Calendar getTimeStamp() {
        return timeStamp;
    }

    @Override
    public String toString() {
        return "HighPriceRecord [highPrice=" + highPrice + ", timeStamp=" + timeStamp + "]";
    }
}