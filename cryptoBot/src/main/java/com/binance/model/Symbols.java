package com.binance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Symbols
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Symbols {

    private String symbol;
    private String status;

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Symbols [status=" + status + ", symbol=" + symbol + "]";
    }
}