package com.binance.strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.binance.model.BullStrategyCoin;
import com.binance.model.CandleStick_1H;
import com.binance.model.PotentialWinningCoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * BullStrategy
 */
@Component
public class BullStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(BullStrategy.class);

    @Value("${bull.strategy.highRecordGain.limit}")
    private Double highRecordGainLimit;

    @Value("${bull.strategy.highPriceRecord.limit}")
    private int highPriceRecordLimit;

    @Value("${bull.strategy.highPriceRecordTime.limit}")
    private int highPriceRecordTimeLimit;

    @Value("${bull.strategy.timeSinceLastTrade.limit}")
    private int timeSinceLastTradeLimit;

    private List<BullStrategyCoin> bullStrategyCoins = new ArrayList<>();

    private StringBuilder data = new StringBuilder("High Price Record Data (Bull Strategy):");

    // Condition 2
    public PotentialWinningCoin checkIfCoinIsTradable(PotentialWinningCoin potentialWinningCoin) {

        for (BullStrategyCoin bullStrategyCoin : bullStrategyCoins) {
            if (bullStrategyCoin.getSymbol().equals(potentialWinningCoin.getSymbol())) {
                long totalTimeInMinutes = 0L;

                totalTimeInMinutes = bullStrategyCoin.getTimeSinceLastTrade().elapsed(TimeUnit.MINUTES);

                if (totalTimeInMinutes >= timeSinceLastTradeLimit) {
                    bullStrategyCoins.remove(bullStrategyCoin);
                    break;
                } else {
                    potentialWinningCoin = null;
                    break;
                }
            }
        }

        return potentialWinningCoin;
    }

    // Condition 3
    // Check if previous hour is no more a gain than the lowest gain found
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

                if (candleStick_1H.getEndOfDayGain() > lowestEndOfHourGain) {
                    potentialWinningCoin = null;
                    break;
                }
            }
        }

        return potentialWinningCoin;
    }

    // Condition 4
    public PotentialWinningCoin checkHighPriceRecordTimeLimit(PotentialWinningCoin potentialWinningCoin) {

        long totalTimeInSeconds = 0;
        if (potentialWinningCoin.getHighPriceRecords().size() == highPriceRecordLimit) {
            totalTimeInSeconds = potentialWinningCoin.getHighPriceRecords().get(0).getStopwatch()
                    .elapsed(TimeUnit.SECONDS);
            if (totalTimeInSeconds > highPriceRecordTimeLimit) {
                potentialWinningCoin = null;

            }
        } else {
            potentialWinningCoin = null;
        }

        return potentialWinningCoin;
    }

    // Condition 5
    public PotentialWinningCoin checkHighPriceRecordsForSignificantGain(PotentialWinningCoin potentialWinningCoin) {

        Double highPriceRecordGain = 0.0;
        if (potentialWinningCoin.getHighPriceRecords().size() > 0) {
            highPriceRecordGain = potentialWinningCoin.getHighPriceRecords()
                    .get(potentialWinningCoin.getHighPriceRecords().size() - 1).getHighPrice()
                    - potentialWinningCoin.getHighPriceRecords().get(0).getHighPrice();
            highPriceRecordGain = (highPriceRecordGain
                    / potentialWinningCoin.getHighPriceRecords().get(0).getHighPrice()) * 100;
        }

        if (highPriceRecordGain < highRecordGainLimit || highPriceRecordGain < restrictedAmount(potentialWinningCoin)) {
            potentialWinningCoin = null;
        } else {
            recordHighPriceRecordGains(highPriceRecordGain, potentialWinningCoin);
            BullStrategyCoin bullStrategyCoin = new BullStrategyCoin();
            bullStrategyCoin.setSymbol(potentialWinningCoin.getSymbol());
            bullStrategyCoins.add(bullStrategyCoin);
        }

        return potentialWinningCoin;
    }

    private Double restrictedAmount(PotentialWinningCoin potentialWinningCoin) {

        Double restrictedAmount = 0.0;

        if (potentialWinningCoin.getSymbol().equals("LINKBTC")) {
            restrictedAmount = 0.52;
        }

        return restrictedAmount;
    }

    private void recordHighPriceRecordGains(Double highPriceRecordGain, PotentialWinningCoin potentialWinningCoin) {

        data.append(" [ (" + potentialWinningCoin.getSymbol() + ") - " + highPriceRecordGain + " ] ");

        LOGGER.info(data.toString());
    }

    @Scheduled(cron = "31 0 0 * * *", zone = "UTC")
    private void resetBullData() {

        data = new StringBuilder("High Price Record Data (Bull Strategy):");
    }
}