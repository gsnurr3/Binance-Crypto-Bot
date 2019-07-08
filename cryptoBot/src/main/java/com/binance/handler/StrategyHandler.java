package com.binance.handler;

import java.util.ArrayList;
import java.util.List;

import com.binance.model.Coin;
import com.binance.model.PotentialWinningCoin;
import com.binance.strategy.BottomOutStrategy;

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

    @Value("${klines.limit}")
    private int limit;

    @Autowired
    private BottomOutStrategy bottomOutStrategy;

    private List<PotentialWinningCoin> potentialWinningCoins = new ArrayList<>();

    // Condition 1
    public List<Coin> checkForNewHighestPriceNewLowestPriceAndUpdateCandleSticks_24H(List<Coin> coins) {

        for (Coin coin : coins) {
            if (coin.getPrices().get(coin.getPrices().size() - 1) > coin.getCandleSticks_24H()
                    .get(coin.getCandleSticks_24H().size() - 1).getHighPrice()) {
                coin.getCandleSticks_24H().get(coin.getCandleSticks_24H().size() - 1)
                        .setHighPrice(coin.getPrices().get(coin.getPrices().size() - 1));

                LOGGER.info("Updating highest price to " + coin.getPrices().get(coin.getPrices().size() - 1) + " for "
                        + coin.getSymbol());

                // if (coin.getCandleSticks_24H().size() == limit) {
                // LOGGER.info("Condition 1 passed. Adding coin to potential winning coins for
                // further evaluation...");

                // PotentialWinningCoin potentialWinningCoin = new
                // PotentialWinningCoin(coin.getSymbol(),
                // coin.getStatus(), coin.getPrices(), coin.getCandleSticks_24H());
                // potentialWinningCoin.setIsHighestPrice(true);
                // potentialWinningCoins.add(potentialWinningCoin);
                // }
            }
            if (coin.getPrices().get(coin.getPrices().size() - 1) < coin.getCandleSticks_24H()
                    .get(coin.getCandleSticks_24H().size() - 1).getLowPrice()) {
                coin.getCandleSticks_24H().get(coin.getCandleSticks_24H().size() - 1)
                        .setLowPrice(coin.getPrices().get(coin.getPrices().size() - 1));

                LOGGER.info("Updating lowest price to " + coin.getPrices().get(coin.getPrices().size() - 1) + " for "
                        + coin.getSymbol());

                if (coin.getCandleSticks_24H().size() == limit) {
                    LOGGER.info("Condition 1 passed. Adding coin to potential winning coins for further evaluation...");

                    PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin(coin.getSymbol(),
                            coin.getStatus(), coin.getPrices(), coin.getCandleSticks_24H());
                    potentialWinningCoin.setIsLowestPrice(true);
                    potentialWinningCoins.add(potentialWinningCoin);
                }
            }
        }

        return coins;
    }

    public PotentialWinningCoin evaluatePotentialWinningCoins() {

        PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin();

        if (potentialWinningCoins.size() >= 1) {

            for (PotentialWinningCoin potentialCoin : potentialWinningCoins) {

                if (potentialCoin.isLowestPrice()) {

                    // Condition 2
                    potentialWinningCoin = bottomOutStrategy.checkCandleStick_24HFromPreviousDay(potentialCoin);

                    if (potentialWinningCoin != null) {
                        LOGGER.info(
                                "Condition 2 passed. Potential winning coin will continue for additional evalutation...");
                    } else {
                        continue;
                    }

                    // Condition 3
                    potentialWinningCoin = bottomOutStrategy.checkIfCoinMarketIsTooBear(potentialCoin);

                    if (potentialWinningCoin != null) {
                        LOGGER.info(
                                "Condition 3 passed. Potential winning coin will continue for additional evalutation...");
                    } else {
                        continue;
                    }

                    // Condition 4
                    potentialWinningCoin = bottomOutStrategy.checkIfTodaysCandleStick_24HIsANewLowRecord(potentialCoin);

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