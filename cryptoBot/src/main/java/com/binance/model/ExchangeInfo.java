package com.binance.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * ExchangeInfo
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeInfo {

    private List<Symbols> symbols = new ArrayList<>();

    public List<Symbols> getSymbols() {
        return symbols;
    }

    public void setSymbols(List<Symbols> symbols) {
        this.symbols = symbols;
    }

    @Override
    public String toString() {
        return "ExchangeInfo [symbols=" + symbols + "]";
    }
}