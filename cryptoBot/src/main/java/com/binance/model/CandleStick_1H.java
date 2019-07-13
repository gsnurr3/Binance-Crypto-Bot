package com.binance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * CandleStick_1H
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandleStick_1H extends CandleStick {

    @Override
    public String toString() {
        return super.toString();
    }
}