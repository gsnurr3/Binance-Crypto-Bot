package com.binance.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Price
 */
public class Price {

    @JsonProperty("symbol")
    private String symbol;

    @JsonProperty("price")
    private Double price;

    public String getSymbol() {
        return symbol;
    }

    public void ListSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPrice() {
        return price;
    }

    public void ListPrice(Double price) {
        this.price = price;
    }
}