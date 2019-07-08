package com.binance.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * KlinesAPI
 */
@Component
public class KlinesAPI extends BaseAPI {

    private final String KLINES_ENDPOINT = getBASE_URL() + "/api/v1/klines";

    private String symbol;

    @Value("${klines.interval}")
    private String interval;

    private Long startTime;
    private Long endTime;

    @Value("${klines.limit}")
    private int limit;

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

    public String getInterval() {
        return interval;
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

    public int getLimit() {
        return limit;
    }

    public String getQueryString() {
        return queryString;
    }

    public void ListQueryString(String queryString) {
        this.queryString = queryString;
    }
}