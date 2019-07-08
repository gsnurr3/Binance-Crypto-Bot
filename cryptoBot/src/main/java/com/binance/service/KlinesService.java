package com.binance.service;

import java.util.List;

import com.binance.dto.KlinesDTO;
import com.binance.model.CandleStick_24H;
import com.binance.model.Coin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * CandleStick_1H_Service
 */
@Service
public class KlinesService {

    @Autowired
    private KlinesDTO klinesDTO;

    public List<Coin> getAllCandleSticks_24H(List<Coin> coins) {

        coins = klinesDTO.getAllCandleSticks_24H(coins);

        for (Coin coin : coins) {
            for (CandleStick_24H candleStick_24H : coin.getCandleSticks_24H()) {
                if (candleStick_24H.getClosePrice() > candleStick_24H.getOpenPrice()) {
                    Double endOfDayGain = 0.0;

                    endOfDayGain = ((candleStick_24H.getClosePrice()- candleStick_24H.getOpenPrice())
                            / candleStick_24H.getOpenPrice()) * 100;
                    candleStick_24H.setEndOfDayGain(endOfDayGain);
                }
                if (candleStick_24H.getClosePrice() < candleStick_24H.getOpenPrice()) {
                    Double endOfDayLoss = 0.0;

                    endOfDayLoss = ((candleStick_24H.getLowPrice() - candleStick_24H.getOpenPrice())
                            / candleStick_24H.getOpenPrice()) * 100;
                    candleStick_24H.setEndOfDayLoss(endOfDayLoss);
                }
            }
        }

        return coins;
    }
}