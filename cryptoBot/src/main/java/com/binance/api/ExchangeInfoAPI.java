package com.binance.api;

import org.springframework.stereotype.Component;

/**
 * ExchangeInfoAPI
 */
@Component
public class ExchangeInfoAPI extends BaseAPI {

    private final String EXCHANGE_INFO_ENDPOINT = getBASE_URL() + "/api/v1/exchangeInfo";

    public String getEXCHANGE_INFO_ENDPOINT() {
        return EXCHANGE_INFO_ENDPOINT;
    }
}