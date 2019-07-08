package com.binance.cron;

import java.util.List;

import com.binance.model.Coin;
import com.binance.service.KlinesService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * CryptoBotScheduler
 */
@Component
public class CandleStick_24HCron {

    @Autowired
    private KlinesService klinesService;

    public List<Coin> updateCandleSticks_24H(List<Coin> coins) {

        coins = klinesService.getAllCandleSticks_24H(coins);

        return coins;
    }
}