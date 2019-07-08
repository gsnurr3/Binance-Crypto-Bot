package com.binance.api;

import org.springframework.stereotype.Component;

/**
 * TestOrderAPI
 */
@Component
public class TestOrderAPI extends BaseAPI {

    private final String TEST_ORDER_ENDPOINT = getBASE_URL() + "/api/v3/order/test";

    private String symbol;

    private Double quantity;

    public String getTEST_ORDER_ENDPOINT() {
        return TEST_ORDER_ENDPOINT;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
}