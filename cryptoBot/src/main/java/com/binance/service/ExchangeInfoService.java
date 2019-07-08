package com.binance.service;

import java.util.List;

import com.binance.dto.ExchangeInfoDTO;
import com.binance.model.Coin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ExchangeInfoService
 */
@Service
public class ExchangeInfoService {

    @Autowired
    private ExchangeInfoDTO exchangeInfoDTO;

    public List<Coin> getExchangeInfo(List<Coin> coins) {

        return exchangeInfoDTO.getExchangeInfo(coins);
    }
}