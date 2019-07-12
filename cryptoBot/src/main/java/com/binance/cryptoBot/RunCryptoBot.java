package com.binance.cryptoBot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.binance.handler.EmailHandler;
import com.binance.handler.StrategyHandler;
import com.binance.handler.TradeHandler;
import com.binance.model.Coin;
import com.binance.model.PotentialWinningCoin;
import com.binance.model.WinningCoin;
import com.binance.service.ExchangeInfoService;
import com.binance.service.KlinesService;
import com.binance.service.PriceService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ApplicationStartup
 */
@Component
public class RunCryptoBot implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(RunCryptoBot.class);

    @Autowired
    private ExchangeInfoService exchangeInfoService;

    @Autowired
    private PriceService priceService;

    @Autowired
    private KlinesService klinesService;

    @Autowired
    private StrategyHandler strategyHandler;

    @Autowired
    private TradeHandler tradeHandler;

    @Autowired
    private EmailHandler emailHandler;

    @Value("${trade.maxLossAllowed}")
    private Double maxLossAllowed;

    @Value("${trade.test.mode}")
    private Boolean testMode;

    private List<Coin> coins = new ArrayList<>();
    private Boolean isInitialized = false;
    private Boolean isTrading = false;
    private Double totalProfit = 0.0;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        LOGGER.info("UTC Time is: " + dateFormat.format(date));
        LOGGER.info("Binance Crypto Bot starting...");

        do {
            if (!isInitialized) {
                coins = exchangeInfoService.getExchangeInfo(coins);
                coins = klinesService.getAllCandleSticks_24H(coins);

                isInitialized = true;

                LOGGER.info("Will update coin(s) every 5 seconds...");
            } else {

                coins = priceService.getAllPrices(coins);
                coins = strategyHandler.checkForNewHighestPriceNewLowestPriceAndUpdateCandleSticks_24H(coins,
                        isTrading);

                PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin();
                potentialWinningCoin = strategyHandler.evaluatePotentialWinningCoins();

                if (potentialWinningCoin != null) {

                    WinningCoin winningCoin = new WinningCoin(potentialWinningCoin.getSymbol(),
                            potentialWinningCoin.getStatus(), potentialWinningCoin.getPrices(),
                            potentialWinningCoin.getCandleSticks_24H());

                    isTrading = true;

                    do {
                        coins = priceService.getAllPrices(coins);
                        coins = strategyHandler.checkForNewHighestPriceNewLowestPriceAndUpdateCandleSticks_24H(coins,
                                isTrading);
                        winningCoin = priceService.updateWinningCoinPrice(coins, winningCoin);
                        winningCoin = tradeHandler.tradeCoin(winningCoin);

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (!winningCoin.isSold());

                    totalProfit = totalProfit + winningCoin.getProfit();

                    LOGGER.info("TOTAL PROFIT: " + totalProfit + "%");
                    LOGGER.info("*****************************************************");

                    if (!testMode) {
                        emailHandler.sendEmail("Sold Coin: " + winningCoin.getSymbol(),
                                "Winning Coin: " + winningCoin.toString() + ", Total profit: " + totalProfit);
                    } else {
                        emailHandler.sendEmail("Sold Coin (Test Mode): " + winningCoin.getSymbol(),
                                "Winning Coin: " + winningCoin.toString() + ", Total profit: " + totalProfit);
                    }

                    coins = klinesService.getAllCandleSticks_24H(coins);

                    isTrading = false;
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (totalProfit > maxLossAllowed);
    }

    // A cron-like expression, extending the usual UN*X definition to include
    // triggers on the second as well as minute, hour, day of month, month and day
    // of week.
    // E.g. "0 * * * * MON-FRI" means once per minute on weekdays (at the top of the
    // minute - the 0th second).
    // Default 20 0 0 * * *
    @Scheduled(cron = "20 0 0 * * *", zone = "UTC")
    private void updateCandleSticksForNewDay() {

        if (!isTrading) {
            LOGGER.info("Getting candle sticks for new day...");
            if (!testMode) {
                emailHandler.sendEmail("Daily Candle Stick Update",
                        "Binance Crypto Bot is currently not trading. Updating candle sticks for new day.");
            } else {
                emailHandler.sendEmail("Daily Candle Stick Update (Test Mode)",
                        "Binance Crypto Bot is currently not trading. Updating candle sticks for new day.");
            }

            coins = klinesService.getAllCandleSticks_24H(coins);
        }
    }
}