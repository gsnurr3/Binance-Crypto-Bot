package com.binance.api;

import org.springframework.stereotype.Component;

/**
 * KlinesAPI
 */
@Component
public class KlinesAPI extends BaseAPI {

    private final String KLINES_ENDPOINT = getBASE_URL() + "/api/v1/klines";

    private String symbol;
    private Long startTime;
    private Long endTime;
    private String queryString;

    public String getKLINES_ENDPOINT() {
        return KLINES_ENDPOINT;
    }

    public String getSymbol() {
        return symbol;
    }

    public void ListSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void ListStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void ListEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public String getQueryString() {
        return queryString;
    }

    public void ListQueryString(String queryString) {
        this.queryString = queryString;
    }
}