package com.binance.handler;

import java.util.ArrayList;
import java.util.List;

import com.binance.model.Coin;
import com.binance.model.PotentialWinningCoin;
import com.binance.strategy.Hourly24BearStrategy;

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

    @Value("${klines.hour.limit}")
    private int hourlyLimit;

    @Value("${klines.day.limit}")
    private int dayLimit;

    @Value("${strategy.minPriceAllowed}")
    private Double minPriceAllowed;

    @Autowired
    private Hourly24BearStrategy hourly24BearStrategy;

    private List<PotentialWinningCoin> potentialWinningCoins = new ArrayList<>();

    // Condition 1
    public List<Coin> checkForNewHighestPriceNewLowestPriceAndUpdateCandleSticks(List<Coin> coins, Boolean isTrading) {

        for (Coin coin : coins) {

            if (coin.getPrices().get(coin.getPrices().size() - 1) >= minPriceAllowed && !isBanned(coin)) {

                if (coin.getPrices().get(coin.getPrices().size() - 1) < coin.getCandleSticks_1H()
                        .get(coin.getCandleSticks_1H().size() - 1).getLowPrice()) {

                    coin.getCandleSticks_1H().get(coin.getCandleSticks_1H().size() - 1)
                            .setLowPrice(coin.getPrices().get(coin.getPrices().size() - 1));

                    if (!isTrading) {
                        LOGGER.info("Updating hourly lowest price to "
                                + coin.getPrices().get(coin.getPrices().size() - 1) + " for " + coin.getSymbol());
                    }

                    if (coin.getCandleSticks_24H().size() == dayLimit
                            && coin.getCandleSticks_1H().size() == hourlyLimit) {

                        PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin(coin.getSymbol(),
                                coin.getStatus(), coin.getPrices(), coin.getCandleSticks_1H(),
                                coin.getCandleSticks_24H());
                        potentialWinningCoin.setIsHourly24Bear(true);

                        potentialWinningCoins.add(potentialWinningCoin);
                    }
                }
            }
        }

        return coins;

    }

    public PotentialWinningCoin evaluatePotentialWinningCoins(Boolean isTrading) {

        String message = "";
        PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin();

        if (potentialWinningCoins.size() >= 1) {

            for (PotentialWinningCoin potentialCoin : potentialWinningCoins) {

                if (!message.isEmpty() && !isTrading) {
                    LOGGER.info(message);
                }

                if (potentialCoin.isHourly24Bear()) {

                    // Condition 2
                    potentialWinningCoin = hourly24BearStrategy.checkIfCoinIsTradable(potentialCoin);

                    if (potentialWinningCoin == null) {
                        // message = "Condition 2 failed. Potential winning coin will be removed from
                        // further evaluation: "
                        // + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 3
                    potentialWinningCoin = hourly24BearStrategy.checkCandleStick_1HFromPreviousHour(potentialCoin);

                    if (potentialWinningCoin == null) {
                        // message = "Condition 3 failed. Potential winning coin will be removed from
                        // further evaluation: "
                        // + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 4
                    potentialWinningCoin = hourly24BearStrategy.checkIfCoinMarketIsTooBear(potentialCoin);

                    if (potentialWinningCoin == null) {
                        // message = "Condition 4 failed. Potential winning coin will be removed from
                        // further evaluation: "
                        // + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 5
                    potentialWinningCoin = hourly24BearStrategy.checkIfCandleStick_1HIsANewLowRecord(potentialCoin);

                    if (potentialWinningCoin != null) {
                        message = "Condition 5 passed. All conditions passed for "
                                + potentialWinningCoin.getSymbol() + " !!!";
                        break;
                    } else {
                        continue;
                    }
                }
            }
        } else {
            potentialWinningCoin = null;
        }

        if (!message.isEmpty() && !isTrading) {
            LOGGER.info(message);
        }

        potentialWinningCoins.clear();

        return potentialWinningCoin;
    }

    private Boolean isBanned(Coin coin) {

        Boolean isBanned = false;

        if (coin.getSymbol().equals("BTCBBTC")) {
            isBanned = true;
        }

        return isBanned;
    }
}