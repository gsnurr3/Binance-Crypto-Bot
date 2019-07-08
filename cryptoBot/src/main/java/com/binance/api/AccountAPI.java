package com.binance.api;

import org.springframework.stereotype.Component;

/**
 * AccountAPI
 */
@Component
public class AccountAPI extends BaseAPI {

    private final String ACCOUNT_ENDPOINT = getBASE_URL() + "/api/v3/account";

    public String getACCOUNT_ENDPOINT() {
        return ACCOUNT_ENDPOINT;
    }
}