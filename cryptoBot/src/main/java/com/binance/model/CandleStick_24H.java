package com.binance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * CandleStick_24H
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CandleStick_24H extends CandleStick {

    @Override
    public String toString() {
        return super.toString();
    }
}