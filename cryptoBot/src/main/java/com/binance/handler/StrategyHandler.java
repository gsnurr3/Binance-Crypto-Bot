package com.binance.handler;

import java.util.ArrayList;
import java.util.List;

import com.binance.model.Coin;
import com.binance.model.PotentialWinningCoin;
import com.binance.strategy.DailyBearStrategy;
import com.binance.strategy.HourlyBearStrategy;

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

    @Value("${strategy.minBullPriceAllowed}")
    private Double minBullPriceAllowed;

    @Value("${strategy.minBearPriceAllowed}")
    private Double minBearPriceAllowed;

    @Value("${hourly.bull.strategy.highPriceRecord.limit}")
    private int highPriceRecordLimit;

    @Autowired
    private HourlyBearStrategy hourlyBearStrategy;

    @Autowired
    private DailyBearStrategy dailyBearStrategy;

    private List<PotentialWinningCoin> potentialWinningCoins = new ArrayList<>();

    // Condition 1
    public List<Coin> checkForNewHighestPriceNewLowestPriceAndUpdateCandleSticks(List<Coin> coins, Boolean isTrading) {

        for (Coin coin : coins) {

            // if (coin.getPrices().get(coin.getPrices().size() - 1) >= minBullPriceAllowed && !isBanned(coin)) {

            //     if (coin.getPrices().get(coin.getPrices().size() - 1) > coin.getCandleSticks_24H()
            //             .get(coin.getCandleSticks_24H().size() - 1).getHighPrice()) {

            //         coin.getCandleSticks_24H().get(coin.getCandleSticks_24H().size() - 1)
            //                 .setHighPrice(coin.getPrices().get(coin.getPrices().size() - 1));
            //     }

            //     if (coin.getPrices().get(coin.getPrices().size() - 1) > coin.getCandleSticks_1H()
            //             .get(coin.getCandleSticks_1H().size() - 1).getHighPrice()) {

            //         if (!isTrading) {
            //             LOGGER.info("Updating hourly highest price to "
            //                     + coin.getPrices().get(coin.getPrices().size() - 1) + " for " + coin.getSymbol());
            //         }

            //         coin.getCandleSticks_1H().get(coin.getCandleSticks_1H().size() - 1)
            //                 .setHighPrice(coin.getPrices().get(coin.getPrices().size() - 1));

            //         if (coin.getHighPriceRecords().size() < highPriceRecordLimit
            //                 || coin.getHighPriceRecords().size() == 0) {
            //             coin.addHighPriceRecord(new HighPriceRecord(coin.getPrices().get(coin.getPrices().size() - 1)));
            //         } else {
            //             coin.getHighPriceRecords().remove(0);
            //             coin.addHighPriceRecord(new HighPriceRecord(coin.getPrices().get(coin.getPrices().size() - 1)));
            //         }

            //         if (RunCryptoBot.isMarketBull) {

            //             PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin(coin.getSymbol(),
            //                     coin.getStatus(), coin.getPrices(), coin.getCandleSticks_1H(),
            //                     coin.getCandleSticks_24H());
            //             potentialWinningCoin.setIsHourlyBull(true);
            //             potentialWinningCoin.setHighPriceRecords(coin.getHighPriceRecords());

            //             potentialWinningCoins.add(potentialWinningCoin);
            //         }
            //     }
            // }

            if (coin.getPrices().get(coin.getPrices().size() - 1) >= minBearPriceAllowed && !isBanned(coin)) {

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
                        potentialWinningCoin.setIsHourlyBear(true);

                        potentialWinningCoins.add(potentialWinningCoin);
                    }
                }

                if (coin.getPrices().get(coin.getPrices().size() - 1) < coin.getCandleSticks_24H()
                        .get(coin.getCandleSticks_24H().size() - 1).getLowPrice()) {

                    coin.getCandleSticks_24H().get(coin.getCandleSticks_24H().size() - 1)
                            .setLowPrice(coin.getPrices().get(coin.getPrices().size() - 1));

                    if (!isTrading) {
                        LOGGER.info("Updating daily lowest price to "
                                + coin.getPrices().get(coin.getPrices().size() - 1) + " for " + coin.getSymbol());
                    }

                    if (coin.getCandleSticks_24H().size() == dayLimit) {

                        PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin(coin.getSymbol(),
                                coin.getStatus(), coin.getPrices(), coin.getCandleSticks_1H(),
                                coin.getCandleSticks_24H());
                        potentialWinningCoin.setIsDailyBear(true);

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

                // if (potentialCoin.isHourlyBull()) {

                //     // Condition 2
                //     potentialWinningCoin = hourlyBullStrategy.checkIfCoinIsTradable(potentialCoin);

                //     if (potentialWinningCoin == null) {
                //         message = "Condition 2 failed. Potential winning coin will be removed from further evaluation: "
                //                 + potentialCoin.getSymbol();
                //         continue;
                //     }

                //     // Condition 3
                //     potentialWinningCoin = hourlyBullStrategy
                //             .checkIfCandleStick_1HFromPreviousHourIsALoss(potentialCoin);

                //     if (potentialWinningCoin == null) {
                //         message = "Condition 3 failed. Potential winning coin will be removed from further evaluation: "
                //                 + potentialCoin.getSymbol();
                //         continue;
                //     }

                //     // Condition 4
                //     potentialWinningCoin = hourlyBullStrategy.checkHighPriceRecordTimeLimit(potentialCoin);

                //     if (potentialWinningCoin == null) {
                //         message = "Condition 4 failed. Potential winning coin will be removed from further evaluation: "
                //                 + potentialCoin.getSymbol();
                //         continue;
                //     }

                //     // Condition 5
                //     potentialWinningCoin = hourlyBullStrategy.checkHighPriceRecordsForSignificantGain(potentialCoin);

                //     if (potentialWinningCoin != null) {
                //         potentialWinningCoin.setMessage("Condition 5 passed. All conditions passed. Buying "
                //                 + potentialWinningCoin.getSymbol() + " !!!");
                //         break;
                //     } else {
                //         continue;
                //     }
                // }

                if (potentialCoin.isHourlyBear()) {

                    // Condition 2
                    potentialWinningCoin = hourlyBearStrategy.checkIfCoinIsTradable(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 2 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 3
                    potentialWinningCoin = hourlyBearStrategy.checkCandleStick_1HFromPreviousHour(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 3 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 4
                    potentialWinningCoin = hourlyBearStrategy.checkIfCoinMarketIsTooBear(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 4 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 5
                    potentialWinningCoin = hourlyBearStrategy.checkIfCandleStick_1HIsANewLowRecord(potentialCoin);

                    if (potentialWinningCoin != null) {
                        message = "Condition 5 passed. All conditions passed. Buying "
                                + potentialWinningCoin.getSymbol() + " !!!";
                        break;
                    } else {
                        continue;
                    }
                }

                if (potentialCoin.isDailyBear()) {

                    // Condition 2
                    potentialWinningCoin = dailyBearStrategy.checkCandleStick_24HFromPreviousDay(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 2 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 3
                    potentialWinningCoin = dailyBearStrategy.checkIfCoinMarketIsTooBear(potentialCoin);

                    if (potentialWinningCoin == null) {
                        message = "Condition 3 failed. Potential winning coin will be removed from further evaluation: "
                                + potentialCoin.getSymbol();
                        continue;
                    }

                    // Condition 4
                    potentialWinningCoin = dailyBearStrategy.checkIfTodaysCandleStick_24HIsANewLowRecord(potentialCoin);

                    if (potentialWinningCoin != null) {
                        message = "Condition 4 passed. All conditions passed. Buying "
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
        if (coin.getSymbol().equals("LINKBTC")) {
            isBanned = true;
        }

        return isBanned;
    }
}