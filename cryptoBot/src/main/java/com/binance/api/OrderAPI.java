package com.binance.api;

import org.springframework.stereotype.Component;

/**
 * OrderAPI
 */
@Component
public class OrderAPI extends BaseAPI {

    private final String ORDER_ENDPOINT = getBASE_URL() + "/api/v3/order";

    private String symbol;

    private Double quantity;

    public String getORDER_ENDPOINT() {
        return ORDER_ENDPOINT;
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