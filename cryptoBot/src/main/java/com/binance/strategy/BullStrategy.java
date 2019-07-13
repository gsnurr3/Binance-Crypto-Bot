package com.binance.strategy;

import com.binance.model.CandleStick_1H;
import com.binance.model.PotentialWinningCoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * BullStrategy
 */
@Component
public class BullStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BullStrategy.class);

    @Value("${bull.strategy.highPriceRecord.limit}")
    private int highPriceRecordLimit;

    // @Value("${bull.strategy.highPriceRecordTime.limit}")
    // private int highPriceRecordTimeLimit;

    // Condition 2
    // Check if previous day from today is no more a gain than the lowest gain found
    public PotentialWinningCoin checkIfCandleStick_1HFromPreviousDayIsALoss(PotentialWinningCoin potentialWinningCoin) {

        Double lowestEndOfHourGain = 0.0;

        for (CandleStick_1H candleStick_1H : potentialWinningCoin.getCandleSticks_1H()) {
            if (potentialWinningCoin.getCandleSticks_1H()
                    .get(potentialWinningCoin.getCandleSticks_1H().size() - 1) != candleStick_1H) {
                if (candleStick_1H.getOpenPrice() <= candleStick_1H.getClosePrice()) {
                    if (lowestEndOfHourGain == 0.0) {
                        lowestEndOfHourGain = candleStick_1H.getEndOfDayGain();
                    } else if (candleStick_1H.getEndOfDayGain() < lowestEndOfHourGain) {
                        lowestEndOfHourGain = candleStick_1H.getEndOfDayGain();
                    }
                }
            }
        }

        for (CandleStick_1H candleStick_1H : potentialWinningCoin.getCandleSticks_1H()) {
            if (potentialWinningCoin.getCandleSticks_1H()
                    .get(potentialWinningCoin.getCandleSticks_1H().size() - 2) == candleStick_1H) {

                LOGGER.info("Prevous candlestick: end of day gain (" + candleStick_1H.getEndOfDayGain()
                        + ") vs lowest end of hour gain (" + lowestEndOfHourGain + ").");
                if (candleStick_1H.getEndOfDayGain() > lowestEndOfHourGain) {
                    LOGGER.info("Condition 2 failed. Potential winning coin will be removed from further evaluation: "
                            + potentialWinningCoin.getSymbol());
                    potentialWinningCoin = null;
                    break;
                }
            }
        }

        return potentialWinningCoin;
    }
}