package com.binance.model;

import com.google.common.base.Stopwatch;

/**
 * BullStrategyCoin
 */
public class StrategyCoinWatcher extends Coin {

    private Stopwatch timeSinceLastTrade = Stopwatch.createStarted();

    public Stopwatch getTimeSinceLastTrade() {
        return timeSinceLastTrade;
    }

    public void setTimeSinceLastTrade(Stopwatch timeSinceLastTrade) {
        this.timeSinceLastTrade = timeSinceLastTrade;
    }

    @Override
    public String toString() {
        return "BullStrategyCoin [timeSinceLastTrade=" + timeSinceLastTrade + "]";
    }
}