package com.binance.api;

import org.springframework.stereotype.Component;

/**
 * PriceAPI
 */
@Component
public class PriceAPI extends BaseAPI {

    private final String PRICE_ENDPOINT = getBASE_URL() + "/api/v3/ticker/price";

    private String symbol;

    public String getPRICE_ENDPOINT() {
        return PRICE_ENDPOINT;
    }

    public String getSymbol() {
        return symbol;
    }

    public void ListSymbol(String symbol) {
        this.symbol = symbol;
    }
}