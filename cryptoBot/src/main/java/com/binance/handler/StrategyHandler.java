package com.binance.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.binance.model.Coin;
import com.binance.model.HighPriceRecord;
import com.binance.model.PotentialWinningCoin;
import com.binance.strategy.BearStrategy;
import com.binance.strategy.BullStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * StrategyHandler
 */
@Component
public class StrategyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(StrategyHandler.class);

    @Value("${klines.day.limit}")
    private int dayLimit;

    @Value("${bull.strategy.highPriceRecord.limit}")
    private int highPriceRecordLimit;

    @Value("${bull.strategy.highPriceRecordTime.limit}")
    private int highPriceRecordTimeLimit;

    @Autowired
    private BullStrategy bullStrategy;

    @Autowired
    private BearStrategy bearStrategy;

    private List<PotentialWinningCoin> potentialWinningCoins = new ArrayList<>();

    // Condition 1
    public List<Coin> checkForNewHighestPriceNewLowestPriceAndUpdateCandleSticks_24H(List<Coin> coins,
            Boolean isTrading) {

        for (Coin coin : coins) {

            if (coin.getPrices().get(coin.getPrices().size() - 1) > coin.getCandleSticks_24H()
                    .get(coin.getCandleSticks_24H().size() - 1).getHighPrice()) {

                coin.getCandleSticks_24H().get(coin.getCandleSticks_24H().size() - 1)
                        .setHighPrice(coin.getPrices().get(coin.getPrices().size() - 1));

                LOGGER.info("Updating daily highest price to " + coin.getPrices().get(coin.getPrices().size() - 1)
                        + " for " + coin.getSymbol());
            }

            // TO DO: Some of this needs moved to bull strategy
            if (coin.getPrices().get(coin.getPrices().size() - 1) > coin.getCandleSticks_1H()
                    .get(coin.getCandleSticks_1H().size() - 1).getHighPrice()) {

                LOGGER.info("Updating hourly highest price to " + coin.getPrices().get(coin.getPrices().size() - 1)
                        + " for " + coin.getSymbol());

                coin.getCandleSticks_1H().get(coin.getCandleSticks_1H().size() - 1)
                        .setHighPrice(coin.getPrices().get(coin.getPrices().size() - 1));

                coin.addHighPriceRecord(new HighPriceRecord(coin.getPrices().get(coin.getPrices().size() - 1)));

                long totalTimeInSeconds = 0;
                if (coin.getHighPriceRecords().size() >= 1) {

                    totalTimeInSeconds = coin.getHighPriceRecords().get(0).getStopwatch().elapsed(TimeUnit.SECONDS);

                    if (totalTimeInSeconds > highPriceRecordTimeLimit) {
                        LOGGER.info("Total time (" + totalTimeInSeconds
                                + ") for records exceeded high price record time limit (" + highPriceRecordTimeLimit
                                + "). Restarting stopwatch and clearing high price records for: " + coin.getSymbol());
                        coin.getHighPriceRecords().clear();
                    } else {
                        if (!isTrading) {
                            if (coin.getHighPriceRecords().size() >= highPriceRecordLimit) {
                                LOGGER.info(
                                        "Condition 1 passed. Adding coin to potential winning coins for further evaluation...");

                                PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin(coin.getSymbol(),
                                        coin.getStatus(), coin.getPrices(), coin.getCandleSticks_1H(),
                                        coin.getCandleSticks_24H());
                                potentialWinningCoin.setIsHighestPrice(true);
                                potentialWinningCoin.setHighPriceRecords(coin.getHighPriceRecords());

                                potentialWinningCoins.add(potentialWinningCoin);
                            }
                        }
                    }
                }
            }

            if (coin.getPrices().get(coin.getPrices().size() - 1) < coin.getCandleSticks_24H()
                    .get(coin.getCandleSticks_24H().size() - 1).getLowPrice()) {

                coin.getCandleSticks_24H().get(coin.getCandleSticks_24H().size() - 1)
                        .setLowPrice(coin.getPrices().get(coin.getPrices().size() - 1));

                LOGGER.info("Updating lowest price to " + coin.getPrices().get(coin.getPrices().size() - 1) + " for "
                        + coin.getSymbol());

                if (!isTrading) {
                    if (coin.getCandleSticks_24H().size() == dayLimit) {
                        LOGGER.info(
                                "Condition 1 passed. Adding coin to potential winning coins for further evaluation...");

                        PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin(coin.getSymbol(),
                                coin.getStatus(), coin.getPrices(), coin.getCandleSticks_1H(),
                                coin.getCandleSticks_24H());
                        potentialWinningCoin.setIsLowestPrice(true);

                        potentialWinningCoins.add(potentialWinningCoin);
                    }
                }
            }
        }

        return coins;

    }

    public PotentialWinningCoin evaluatePotentialWinningCoins() {

        PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin();

        if (potentialWinningCoins.size() >= 1) {

            for (PotentialWinningCoin potentialCoin : potentialWinningCoins) {

                if (potentialCoin.isHighestPrice()) {

                    // Condition 2
                    potentialWinningCoin = bullStrategy.checkIfCandleStick_1HFromPreviousDayIsALoss(potentialCoin);

                    if (potentialWinningCoin != null) {
                        LOGGER.info("Condition 2 passed. All conditions passed. Buying " + potentialCoin.getSymbol()
                                + " !!!");
                        break;
                    } else {
                        continue;
                    }

                    // // Condition 2
                    // potentialWinningCoin =
                    // bullStrategy.checkIfCandleStick_1HFromPreviousDayIsALoss(potentialCoin);

                    // if (potentialWinningCoin != null) {
                    // LOGGER.info(
                    // "Condition 2 passed. Potential winning coin will continue for additional
                    // evalutation...");
                    // } else {
                    // continue;
                    // }
                }

                if (potentialCoin.isLowestPrice()) {

                    // Condition 2
                    potentialWinningCoin = bearStrategy.checkCandleStick_24HFromPreviousDay(potentialCoin);

                    if (potentialWinningCoin != null) {
                        LOGGER.info(
                                "Condition 2 passed. Potential winning coin will continue for additional evalutation...");
                    } else {
                        continue;
                    }

                    // Condition 3
                    potentialWinningCoin = bearStrategy.checkIfCoinMarketIsTooBear(potentialCoin);

                    if (potentialWinningCoin != null) {
                        LOGGER.info(
                                "Condition 3 passed. Potential winning coin will continue for additional evalutation...");
                    } else {
                        continue;
                    }

                    // Condition 4
                    potentialWinningCoin = bearStrategy.checkIfTodaysCandleStick_24HIsANewLowRecord(potentialCoin);

                    if (potentialWinningCoin != null) {
                        LOGGER.info("Condition 4 passed. All conditions passed. Buying " + potentialCoin.getSymbol()
                                + " !!!");
                        break;
                    } else {
                        continue;
                    }
                }
            }
        } else {
            potentialWinningCoin = null;
            // LOGGER.info("No coins have passed condition 1. Will scan coins again
            // shortly...");
        }

        potentialWinningCoins.clear();

        return potentialWinningCoin;
    }
}