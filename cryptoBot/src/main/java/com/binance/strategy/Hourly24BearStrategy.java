package com.binance.strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.binance.handler.EmailHandler;
import com.binance.model.CandleStick_1H;
import com.binance.model.PotentialWinningCoin;
import com.binance.model.StrategyCoinWatcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * HourlyBearStrategy
 */
@Component
public class Hourly24BearStrategy {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hourly24BearStrategy.class);

    @Autowired
    EmailHandler emailHandler;

    @Value("${hourly.24.bear.strategy.timeSinceLastTrade.limit}")
    private int timeSinceLastTradeLimit;

    private List<StrategyCoinWatcher> strategyCoinWatchers = new ArrayList<>();
    private List<Double> endOfHourDifferences = new ArrayList<>();
    private Double dynamicEndOfHourLossRecord = 0.0;

    // Condition 2
    public PotentialWinningCoin checkIfCoinIsTradable(PotentialWinningCoin potentialWinningCoin) {

        for (StrategyCoinWatcher strategyCoinWatcher : strategyCoinWatchers) {
            if (strategyCoinWatcher.getSymbol().equals(potentialWinningCoin.getSymbol())) {
                long totalTimeInMinutes = 0L;

                totalTimeInMinutes = strategyCoinWatcher.getTimeSinceLastTrade().elapsed(TimeUnit.MINUTES);

                if (totalTimeInMinutes >= timeSinceLastTradeLimit) {
                    strategyCoinWatchers.remove(strategyCoinWatcher);
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

    // Condition 4
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

    // Condition 5
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

                    Double record = recordEndOfHourLossDifference(hourLoss, lowestEndOfHourLoss);

                    if (dynamicEndOfHourLossRecord == 0.0) {
                        LOGGER.info(potentialWinningCoin.getSymbol() + " / Hour loss: " + hourLoss
                                + " / Lowest end of hour loss: " + lowestEndOfHourLoss);
                    } else {
                        LOGGER.info(potentialWinningCoin.getSymbol() + " / (Dynamic) - Hour loss: " + record
                                + " / Buy range: [ " + dynamicEndOfHourLossRecord + " ] - [ "
                                + (dynamicEndOfHourLossRecord + 15) + " ]");
                    }

                    Double currentChange = record - dynamicEndOfHourLossRecord;

                    if (dynamicEndOfHourLossRecord == 0.0) {
                        potentialWinningCoin = null;
                        break;
                    } else {
                        if (currentChange > 15) {
                            StrategyCoinWatcher strategyCoinWatcher = new StrategyCoinWatcher();
                            strategyCoinWatcher.setSymbol(potentialWinningCoin.getSymbol());
                            strategyCoinWatchers.add(strategyCoinWatcher);
                        } else if (currentChange >= -0.0 && currentChange <= 15) {
                            StrategyCoinWatcher strategyCoinWatcher = new StrategyCoinWatcher();
                            strategyCoinWatcher.setSymbol(potentialWinningCoin.getSymbol());
                            strategyCoinWatchers.add(strategyCoinWatcher);
                        }

                        if (currentChange < -0.0 || currentChange > 15) {
                            potentialWinningCoin = null;
                            break;
                        }
                    }

                    if (dynamicEndOfHourLossRecord != 0.0
                            && endOfHourDifferences.get(endOfHourDifferences.size() - 1) > dynamicEndOfHourLossRecord) {
                        dynamicEndOfHourLossRecord = endOfHourDifferences.get(endOfHourDifferences.size() - 1);
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
        Double currentChange = record - dynamicEndOfHourLossRecord;

        if (currentChange <= 15) {
            endOfHourDifferences.add(record);

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

        dynamicEndOfHourLossRecord = endOfHourDifferences.get(endOfHourDifferences.size() - 1);

        endOfHourDifferences.clear();
    }
}