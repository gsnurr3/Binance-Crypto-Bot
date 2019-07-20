package com.binance.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.binance.cryptoBot.RunCryptoBot;
import com.binance.model.CandleStick_1H;
import com.binance.model.PotentialWinningCoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * HourlyBearStrategy
 */
@Component
public class HourlyBearStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(HourlyBearStrategy.class);

    private List<Double> endOfHourDifferences = new ArrayList<>();

    private Double dynamicEndOfHourLossRecord = 0.0;

    @Value("${binance.crypto.bot.marketBullValue}")
    private Double marketBullValue;

    @Value("${hourly.bear.strategy.maxChangeAllowed}")
    private int maxChangeAllowed;

    @Value("${hourly.bear.strategy.lowestEndOfHourLossAccuracy}")
    private Double lowestEndOfHourLossAccuracy;

    // Condition 2
    // Check if previous day from today is no more a gain than the lowest gain found
    public PotentialWinningCoin checkCandleStick_1HFromPreviousHour(PotentialWinningCoin potentialWinningCoin) {

        Double lowestEndOfDayGain = 0.0;

        for (CandleStick_1H candleStick_1H : potentialWinningCoin.getCandleSticks_1H()) {
            if (potentialWinningCoin.getCandleSticks_1H()
                    .get(potentialWinningCoin.getCandleSticks_1H().size() - 2) == candleStick_1H) {
                if (candleStick_1H.getEndOfCandleStickGain() > lowestEndOfDayGain) {
                    potentialWinningCoin = null;
                    break;
                }
            }
        }

        return potentialWinningCoin;
    }

    // Condition 3
    public PotentialWinningCoin checkIfCoinMarketIsTooBear(PotentialWinningCoin potentialWinningCoin) {

        int count = 0;

        for (CandleStick_1H candleStick_1H : potentialWinningCoin.getCandleSticks_1H()) {
            if (potentialWinningCoin.getCandleSticks_1H()
                    .get(potentialWinningCoin.getCandleSticks_1H().size() - 3) == candleStick_1H) {
                if (candleStick_1H.getOpenPrice() > candleStick_1H.getClosePrice()) {

                    count++;
                }
            }
            if (potentialWinningCoin.getCandleSticks_1H()
                    .get(potentialWinningCoin.getCandleSticks_1H().size() - 4) == candleStick_1H) {
                if (candleStick_1H.getOpenPrice() > candleStick_1H.getClosePrice()) {

                    count++;
                }
            }
            if (potentialWinningCoin.getCandleSticks_1H()
                    .get(potentialWinningCoin.getCandleSticks_1H().size() - 5) == candleStick_1H) {
                if (candleStick_1H.getOpenPrice() > candleStick_1H.getClosePrice()) {

                    count++;
                }
            }
            if (potentialWinningCoin.getCandleSticks_1H()
                    .get(potentialWinningCoin.getCandleSticks_1H().size() - 6) == candleStick_1H) {
                if (candleStick_1H.getOpenPrice() > candleStick_1H.getClosePrice()) {

                    count++;
                }
            }
        }

        if (count >= 4) {
            potentialWinningCoin = null;
        }

        return potentialWinningCoin;
    }

    // Condition 3
    // Check if today is a new low record vs all candle sticks obtained
    public PotentialWinningCoin checkIfCandleStick_1HIsANewLowRecord(PotentialWinningCoin potentialWinningCoin) {

        Double lowestEndOfHourLoss = 0.0;

        for (CandleStick_1H candleStick_1H : potentialWinningCoin.getCandleSticks_1H()) {
            if (potentialWinningCoin.getCandleSticks_1H()
                    .get(potentialWinningCoin.getCandleSticks_1H().size() - 1) != candleStick_1H) {
                if (candleStick_1H.getOpenPrice() > candleStick_1H.getClosePrice()) {
                    if (lowestEndOfHourLoss == 0.0) {
                        lowestEndOfHourLoss = candleStick_1H.getEndOfCandleStickLoss();
                    } else if (candleStick_1H.getEndOfCandleStickLoss() < lowestEndOfHourLoss) {
                        lowestEndOfHourLoss = candleStick_1H.getEndOfCandleStickLoss();
                    }
                }
            }
        }

        if (lowestEndOfHourLoss != 0.0) {
            for (CandleStick_1H candleStick_1H : potentialWinningCoin.getCandleSticks_1H()) {
                if (potentialWinningCoin.getCandleSticks_1H()
                        .get(potentialWinningCoin.getCandleSticks_1H().size() - 1) == candleStick_1H) {

                    Double hourLoss = ((candleStick_1H.getLowPrice() - candleStick_1H.getOpenPrice())
                            / candleStick_1H.getOpenPrice()) * 100;

                    lowestEndOfHourLoss = lowestEndOfHourLoss * lowestEndOfHourLossAccuracy;

                    Double record = recordEndOfHourLossDifference(hourLoss, lowestEndOfHourLoss);

                    if (record <= maxChangeAllowed) {
                        if (dynamicEndOfHourLossRecord == 0.0) {
                            LOGGER.info("Hour loss: " + hourLoss + ", Lowest end of hour loss: " + lowestEndOfHourLoss);
                        } else {
                            LOGGER.info("(Dynamic) - Hour loss: " + record + ", Lowest end of hour loss: "
                                    + dynamicEndOfHourLossRecord);
                        }
                    }

                    if (dynamicEndOfHourLossRecord != 0.0 && (record < dynamicEndOfHourLossRecord)) {
                        potentialWinningCoin = null;
                        break;
                    }

                    if (dynamicEndOfHourLossRecord == 0.0
                            && (hourLoss > lowestEndOfHourLoss || record > maxChangeAllowed)) {
                        potentialWinningCoin = null;
                        break;
                    }

                    if (dynamicEndOfHourLossRecord != 0.0
                            && endOfHourDifferences.get(endOfHourDifferences.size() - 1) > dynamicEndOfHourLossRecord) {
                        dynamicEndOfHourLossRecord = endOfHourDifferences.get(endOfHourDifferences.size() - 1) + 5;
                    }
                }
            }
        } else {
            potentialWinningCoin = null;
        }

        return potentialWinningCoin;
    }

    private Double recordEndOfHourLossDifference(Double hourLoss, Double lowestEndOfHourLoss) {

        Double record = ((hourLoss - lowestEndOfHourLoss) / lowestEndOfHourLoss) * 100;

        if (record <= maxChangeAllowed) {
            endOfHourDifferences.add(record);

            if (record > marketBullValue) {
                RunCryptoBot.isMarketBull = false;
                LOGGER.info("Market found to be bear. Disabling bull strategy!");
            }

            Collections.sort(endOfHourDifferences);

            if (endOfHourDifferences.size() > 10) {
                do {
                    endOfHourDifferences.remove(0);
                } while (endOfHourDifferences.size() > 10);
            }

            StringBuilder data = new StringBuilder();

            data.append("End of Hour Difference Data (Hourly Bear Strategy):");

            for (Double endOfHourDifference : endOfHourDifferences) {
                data.append(" [ " + endOfHourDifference + " ] ");
            }

            LOGGER.info(data.toString());
        }

        return record;
    }

    @Scheduled(cron = "10 0 * * * *", zone = "UTC")
    private void resetHourlyBearData() {

        RunCryptoBot.isMarketBull = true;

        for (Double endOfHourDifference : endOfHourDifferences) {
            if (endOfHourDifference > marketBullValue) {
                RunCryptoBot.isMarketBull = false;
            }
        }

        if (RunCryptoBot.isMarketBull) {
            LOGGER.info("Market found to be bull (" + RunCryptoBot.isMarketBull + "). Enabling bull strategy!");
        } else {
            LOGGER.info("Market found to be bear (" + RunCryptoBot.isMarketBull + "). Disabling bull strategy!");
        }

        dynamicEndOfHourLossRecord = endOfHourDifferences.get(endOfHourDifferences.size() - 1) + 5;

        endOfHourDifferences.clear();
    }
}