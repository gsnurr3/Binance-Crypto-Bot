package com.binance.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.binance.model.CandleStick_24H;
import com.binance.model.PotentialWinningCoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * BearStrategy
 */
@Component
public class DailyBearStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(DailyBearStrategy.class);

    private List<Double> endOfDayDifferences = new ArrayList<>();

    @Value("${daily.bear.strategy.lowestEndOfDayLossAccuracy}")
    private Double lowestEndOfDayLossAccuracy;

    // Condition 2
    // Check if previous day from today is no more a gain than the lowest gain found
    public PotentialWinningCoin checkCandleStick_24HFromPreviousDay(PotentialWinningCoin potentialWinningCoin) {

        Double lowestEndOfDayGain = 0.0;

        for (CandleStick_24H candleStick_24H : potentialWinningCoin.getCandleSticks_24H()) {
            if (potentialWinningCoin.getCandleSticks_24H()
                    .get(potentialWinningCoin.getCandleSticks_24H().size() - 1) != candleStick_24H) {
                if (candleStick_24H.getOpenPrice() <= candleStick_24H.getClosePrice()) {
                    if (lowestEndOfDayGain == 0.0) {
                        lowestEndOfDayGain = candleStick_24H.getEndOfCandleStickGain();
                    } else if (candleStick_24H.getEndOfCandleStickGain() < lowestEndOfDayGain) {
                        lowestEndOfDayGain = candleStick_24H.getEndOfCandleStickGain();
                    }
                }
            }
        }

        for (CandleStick_24H candleStick_24H : potentialWinningCoin.getCandleSticks_24H()) {
            if (potentialWinningCoin.getCandleSticks_24H()
                    .get(potentialWinningCoin.getCandleSticks_24H().size() - 2) == candleStick_24H) {
                if (candleStick_24H.getEndOfCandleStickGain() > lowestEndOfDayGain) {
                    potentialWinningCoin = null;
                    break;
                }
            }
        }

        return potentialWinningCoin;
    }

    // Condition 3
    // Check if previous several days have a closingPrice < openPrice
    public PotentialWinningCoin checkIfCoinMarketIsTooBear(PotentialWinningCoin potentialWinningCoin) {

        int count = 0;

        for (CandleStick_24H candleStick_24H : potentialWinningCoin.getCandleSticks_24H()) {
            if (potentialWinningCoin.getCandleSticks_24H()
                    .get(potentialWinningCoin.getCandleSticks_24H().size() - 3) == candleStick_24H) {
                if (candleStick_24H.getOpenPrice() > candleStick_24H.getClosePrice()) {

                    count++;
                }
            }
            if (potentialWinningCoin.getCandleSticks_24H()
                    .get(potentialWinningCoin.getCandleSticks_24H().size() - 4) == candleStick_24H) {
                if (candleStick_24H.getOpenPrice() > candleStick_24H.getClosePrice()) {

                    count++;
                }
            }
            if (potentialWinningCoin.getCandleSticks_24H()
                    .get(potentialWinningCoin.getCandleSticks_24H().size() - 5) == candleStick_24H) {
                if (candleStick_24H.getOpenPrice() > candleStick_24H.getClosePrice()) {

                    count++;
                }
            }
            if (potentialWinningCoin.getCandleSticks_24H()
                    .get(potentialWinningCoin.getCandleSticks_24H().size() - 6) == candleStick_24H) {
                if (candleStick_24H.getOpenPrice() > candleStick_24H.getClosePrice()) {

                    count++;
                }
            }
        }

        if (count >= 4) {
            potentialWinningCoin = null;
        }

        return potentialWinningCoin;
    }

    // Condition 4
    // Check if today is a new low record vs all candle sticks obtained
    public PotentialWinningCoin checkIfTodaysCandleStick_24HIsANewLowRecord(PotentialWinningCoin potentialWinningCoin) {

        Double lowestEndOfDayLoss = 0.0;

        for (CandleStick_24H candleStick_24H : potentialWinningCoin.getCandleSticks_24H()) {
            if (potentialWinningCoin.getCandleSticks_24H()
                    .get(potentialWinningCoin.getCandleSticks_24H().size() - 1) != candleStick_24H) {
                if (candleStick_24H.getOpenPrice() > candleStick_24H.getClosePrice()) {
                    if (lowestEndOfDayLoss == 0.0) {
                        lowestEndOfDayLoss = candleStick_24H.getEndOfCandleStickLoss();
                    } else if (candleStick_24H.getEndOfCandleStickLoss() < lowestEndOfDayLoss) {
                        lowestEndOfDayLoss = candleStick_24H.getEndOfCandleStickLoss();
                    }
                }
            }
        }

        if (lowestEndOfDayLoss != 0.0) {
            for (CandleStick_24H candleStick_24H : potentialWinningCoin.getCandleSticks_24H()) {
                if (potentialWinningCoin.getCandleSticks_24H()
                        .get(potentialWinningCoin.getCandleSticks_24H().size() - 1) == candleStick_24H) {

                    Double todaysLoss = ((candleStick_24H.getLowPrice() - candleStick_24H.getOpenPrice())
                            / candleStick_24H.getOpenPrice()) * 100;

                    lowestEndOfDayLoss = lowestEndOfDayLoss * lowestEndOfDayLossAccuracy;

                    recordEndOfDayDifferences(todaysLoss, lowestEndOfDayLoss);

                    LOGGER.info("Todays loss: " + todaysLoss + ", Lowest end of day loss: " + lowestEndOfDayLoss);

                    if (todaysLoss > lowestEndOfDayLoss) {
                        potentialWinningCoin = null;
                        break;
                    }
                }
            }
        } else {
            potentialWinningCoin = null;
        }

        return potentialWinningCoin;
    }

    private void recordEndOfDayDifferences(Double todaysLoss, Double lowestEndOfDayLoss) {

        Double record = ((todaysLoss - lowestEndOfDayLoss) / lowestEndOfDayLoss) * 100;
        endOfDayDifferences.add(record);

        Collections.sort(endOfDayDifferences);

        if (endOfDayDifferences.size() > 10) {
            do {
                endOfDayDifferences.remove(0);
            } while (endOfDayDifferences.size() > 10);
        }

        StringBuilder data = new StringBuilder();

        data.append("End of Day Difference Data (Daily Bear Strategy):");

        for (Double endOfDayDifference : endOfDayDifferences) {
            data.append(" [ " + endOfDayDifference + " ] ");
        }

        LOGGER.info(data.toString());
    }

    @Scheduled(cron = "30 0 0 * * *", zone = "UTC")
    private void resetBearData() {

        endOfDayDifferences.clear();
    }
}