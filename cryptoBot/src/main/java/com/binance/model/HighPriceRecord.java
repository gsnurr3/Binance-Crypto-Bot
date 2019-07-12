package com.binance.model;

import java.util.Calendar;

import com.google.common.base.Stopwatch;

/**
 * HighPriceRecord
 */
public class HighPriceRecord {

    private Double highPrice;
    private Calendar timeStamp;
    private Stopwatch stopwatch;

    public HighPriceRecord(Double highPrice) {
        this.highPrice = highPrice;
        this.timeStamp = Calendar.getInstance();
        this.stopwatch = Stopwatch.createStarted();
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

    public Stopwatch getStopwatch() {
        return stopwatch;
    }

    public void setStopwatch(Stopwatch stopwatch) {
        this.stopwatch = stopwatch;
    }

    @Override
    public String toString() {
        return "HighPriceRecord [highPrice=" + highPrice + ", stopwatch=" + stopwatch + ", timeStamp=" + timeStamp
                + "]";
    }
}