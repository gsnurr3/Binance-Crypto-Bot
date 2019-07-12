package com.binance.strategy;

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
    // Checks to see if the size of the high price record array meets the high price
    // record limit
    public PotentialWinningCoin checkHighPriceRecordSize(PotentialWinningCoin potentialWinningCoin) {

        if (potentialWinningCoin.getHighPriceRecords().size() < highPriceRecordLimit) {
            LOGGER.info("Condition 2 failed due to high price record size ("
                    + potentialWinningCoin.getHighPriceRecords().size() + ") being less than limit ("
                    + highPriceRecordLimit + "). Potential winning coin will be removed from further evaluation: "
                    + potentialWinningCoin.getSymbol());
            potentialWinningCoin = null;
        }

        return potentialWinningCoin;
    }

    // Condition 3
    // Compares the first and last calendar instances to see if the high prices were
    // recorded in the desired time frame
    // public PotentialWinningCoin
    // compareHighPriceRecordFirstIndexAndLastIndexCalendarInstance(
    // PotentialWinningCoin potentialWinningCoin) {

    // Date d1 =
    // potentialWinningCoin.getHighPriceRecords().get(0).getTimeStamp().getTime();
    // Date d2 =
    // potentialWinningCoin.getHighPriceRecords().get(potentialWinningCoin.getHighPriceRecords().size()
    // - 1)
    // .getTimeStamp().getTime();

    // long diffSeconds = 0L;

    // try {
    // // in milliseconds
    // long diff = d2.getTime() - d1.getTime();

    // diffSeconds = diff / 1000 % 60;
    // LOGGER.info("High price records were recorded over (seconds): " +
    // diffSeconds);
    // } catch (Exception e) {
    // emailHandler.sendEmail("Error", "Issue comparing dates in bull strategy. Kill
    // and check Binance Crypto Bot ASAP!");
    // e.printStackTrace();
    // }

    // if (diffSeconds > highPriceRecordTimeLimit) {
    // LOGGER.info("Condition 3 failed. Potential winning coin will be removed from
    // further evaluation: "
    // + potentialWinningCoin.getSymbol());
    // potentialWinningCoin = null;
    // }

    // return potentialWinningCoin;
    // }
}