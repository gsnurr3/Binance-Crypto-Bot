package com.binance.handler;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private BullStrategy bullStrategy;

    @Autowired
    private BearStrategy bearStrategy;

    private List<PotentialWinningCoin> potentialWinningCoins = new ArrayList<>();

    // Condition 1
    public List<Coin> checkForNewHighestPriceNewLowestPriceAndUpdateCandleSticks(List<Coin> coins, Boolean isTrading) {

        for (Coin coin : coins) {

            if (coin.getPrices().get(coin.getPrices().size() - 1) > coin.getCandleSticks_24H()
                    .get(coin.getCandleSticks_24H().size() - 1).getHighPrice()) {

                coin.getCandleSticks_24H().get(coin.getCandleSticks_24H().size() - 1)
                        .setHighPrice(coin.getPrices().get(coin.getPrices().size() - 1));
            }

            if (coin.getPrices().get(coin.getPrices().size() - 1) > coin.getCandleSticks_1H()
                    .get(coin.getCandleSticks_1H().size() - 1).getHighPrice()) {

                LOGGER.info("Updating hourly highest price to " + coin.getPrices().get(coin.getPrices().size() - 1)
                        + " for " + coin.getSymbol());

                coin.getCandleSticks_1H().get(coin.getCandleSticks_1H().size() - 1)
                        .setHighPrice(coin.getPrices().get(coin.getPrices().size() - 1));

                if (coin.getHighPriceRecords().size() < highPriceRecordLimit
                        || coin.getHighPriceRecords().size() == 0) {
                    coin.addHighPriceRecord(new HighPriceRecord(coin.getPrices().get(coin.getPrices().size() - 1)));
                } else {
                    coin.getHighPriceRecords().remove(0);
                    coin.addHighPriceRecord(new HighPriceRecord(coin.getPrices().get(coin.getPrices().size() - 1)));
                }

                if (!isTrading) {
                    LOGGER.info("Condition 1 passed. Adding coin to potential winning coins for further evaluation: "
                            + coin.getSymbol());

                    PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin(coin.getSymbol(),
                            coin.getStatus(), coin.getPrices(), coin.getCandleSticks_1H(), coin.getCandleSticks_24H());
                    potentialWinningCoin.setIsHighestPrice(true);
                    potentialWinningCoin.setHighPriceRecords(coin.getHighPriceRecords());

                    potentialWinningCoins.add(potentialWinningCoin);
                }
            }

            if (coin.getPrices().get(coin.getPrices().size() - 1) < coin.getCandleSticks_24H()
                    .get(coin.getCandleSticks_24H().size() - 1).getLowPrice()) {

                coin.getCandleSticks_24H().get(coin.getCandleSticks_24H().size() - 1)
                        .setLowPrice(coin.getPrices().get(coin.getPrices().size() - 1));

                LOGGER.info("Updating daily lowest price to " + coin.getPrices().get(coin.getPrices().size() - 1)
                        + " for " + coin.getSymbol());

                if (!isTrading) {
                    if (coin.getCandleSticks_24H().size() == dayLimit) {
                        LOGGER.info(
                                "Condition 1 passed. Adding coin to potential winning coins for further evaluation: "
                                        + coin.getSymbol());

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

        String message = "";
        PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin();

        if (potentialWinningCoins.size() >= 1) {

            for (PotentialWinningCoin potentialCoin : potentialWinningCoins) {

                if (!message.isEmpty()) {
                    LOGGER.info(message);
                }

                if (potentialCoin.isHighestPrice()) {

                    // Condition 2
                    potentialWinningCoin = bullStrategy.checkIfCoinIsTradable(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 2 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 3
                    potentialWinningCoin = bullStrategy.checkIfCandleStick_1HFromPreviousDayIsALoss(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 3 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 4
                    potentialWinningCoin = bullStrategy.checkHighPriceRecordTimeLimit(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 4 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 5
                    potentialWinningCoin = bullStrategy.checkHighPriceRecordsForSignificantGain(potentialCoin);

                    if (potentialWinningCoin != null) {
                        potentialWinningCoin.setMessage("Condition 5 passed. All conditions passed. Buying "
                                + potentialWinningCoin.getSymbol() + " !!!");
                        break;
                    } else {
                        continue;
                    }
                }

                if (potentialCoin.isLowestPrice()) {

                    // Condition 2
                    potentialWinningCoin = bearStrategy.checkCandleStick_24HFromPreviousDay(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 2 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 3
                    potentialWinningCoin = bearStrategy.checkIfCoinMarketIsTooBear(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 3 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 4
                    potentialWinningCoin = bearStrategy.checkIfTodaysCandleStick_24HIsANewLowRecord(potentialCoin);

                    if (potentialWinningCoin != null) {
                        potentialWinningCoin.setMessage("Condition 4 passed. All conditions passed. Buying "
                                + potentialWinningCoin.getSymbol() + " !!!");
                        break;
                    } else {
                        continue;
                    }
                }
            }
        } else {
            potentialWinningCoin = null;
        }

        if (!message.isEmpty()) {
            LOGGER.info(message);
        }

        potentialWinningCoins.clear();

        return potentialWinningCoin;
    }
}