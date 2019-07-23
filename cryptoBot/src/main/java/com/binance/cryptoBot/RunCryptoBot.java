package com.binance.cryptoBot;

import java.io.IOException;
import java.net.SocketTimeoutException;
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

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

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
    private List<WinningCoin> soldCoins = new ArrayList<>();
    private Boolean isInitialized = false;
    private Boolean isTrading = false;
    private Double totalProfit = 0.0;

    @Override
    public void onApplicationEvent(final ApplicationReadyEvent event) {

        do {
            if (!isInitialized) {

                Date date = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

                LOGGER.info("UTC Time is: " + dateFormat.format(date));
                LOGGER.info("Binance Crypto Bot starting...");

                coins = exchangeInfoService.getExchangeInfo(coins);

                coins = klinesService.getAllCandleSticks_1H(coins);
                coins = klinesService.getAllCandleSticks_24H(coins);

                isInitialized = true;

                LOGGER.info("Will update coin(s) every 5 seconds...");
            } else {

                coins = priceService.getAllPrices(coins);
                coins = strategyHandler.checkForNewHighestPriceNewLowestPriceAndUpdateCandleSticks(coins, isTrading);

                PotentialWinningCoin potentialWinningCoin = new PotentialWinningCoin();
                potentialWinningCoin = strategyHandler.evaluatePotentialWinningCoins(isTrading);

                if (potentialWinningCoin != null) {

                    WinningCoin winningCoin = new WinningCoin(potentialWinningCoin.getSymbol(),
                            potentialWinningCoin.getStatus(), potentialWinningCoin.getPrices(),
                            potentialWinningCoin.getCandleSticks_1H(), potentialWinningCoin.getCandleSticks_24H());
                    winningCoin.setIsHourly24Bear(potentialWinningCoin.isHourly24Bear());

                    isTrading = true;

                    do {
                        coins = priceService.getAllPrices(coins);
                        winningCoin = priceService.updateWinningCoinPrice(coins, winningCoin);

                        try {
                            winningCoin = tradeHandler.tradeCoin(winningCoin);
                        } catch (ResourceAccessException | SocketTimeoutException | ConnectTimeoutException
                                | NullPointerException e1) {
                            LOGGER.error(e1.toString());
                            emailHandler.sendEmail("Error", e1.toString());
                            isTrading = false;
                            this.onApplicationEvent(event);
                        } catch (IOException e2) {
                            LOGGER.error(e2.toString());
                            emailHandler.sendEmail("Error", e2.toString());
                            isTrading = false;
                            this.onApplicationEvent(event);
                        }

                        coins = strategyHandler.checkForNewHighestPriceNewLowestPriceAndUpdateCandleSticks(coins,
                                isTrading);
                        strategyHandler.evaluatePotentialWinningCoins(isTrading);

                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } while (!winningCoin.isSold());

                    totalProfit = totalProfit + winningCoin.getProfit();

                    soldCoins.add(winningCoin);

                    LOGGER.info("TOTAL PROFIT: " + totalProfit + "%");
                    LOGGER.info("*****************************************************");

                    if (testMode) {
                        emailHandler.sendEmail("Sold Coin (Test Mode): " + winningCoin.getSymbol(),
                                "Winning Coin: " + winningCoin.toString() + ", Total profit: " + totalProfit + "%");
                    }

                    coins = klinesService.getAllCandleSticks_1H(coins);

                    isTrading = false;
                }
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (totalProfit > maxLossAllowed);

        if (!testMode) {
            emailHandler.sendEmail("Max Loss Breached - Shutting Down Binance Crypto Bot",
                    "Binance Crypto Bot has a max loss allowed of " + maxLossAllowed + ". The total loss has been "
                            + totalProfit + ".");
        } else {
            emailHandler.sendEmail("Max Loss Breached - Shutting Down Binance Crypto Bot (Test Mode)",
                    "Binance Crypto Bot has a max loss allowed of " + maxLossAllowed + ". The total loss has been "
                            + totalProfit + ".");
        }
    }

    // A cron-like expression, extending the usual UN*X definition to include
    // triggers on the second as well as minute, hour, day of month, month and day
    // of week.
    // E.g. "0 * * * * MON-FRI" means once per minute on weekdays (at the top of the
    // minute - the 0th second).
    // Default 20 0 0 * * *
    @Scheduled(cron = "15 0 * * * *", zone = "UTC")
    private void updateCandleSticksForNewHour() {

        if (!isTrading) {
            coins = klinesService.getAllCandleSticks_1H(coins);
        }
    }

    @Scheduled(cron = "20 0 0 * * *", zone = "UTC")
    private void updateCandleSticksForNewDay() {

        if (!isTrading) {
            coins = klinesService.getAllCandleSticks_24H(coins);
        }
    }

    @Scheduled(cron = "0 0 12 * * *", zone = "UTC")
    private void sendHealthStatus() {

        if (!testMode) {
            emailHandler.sendEmail("Health Status", "Binance Crypto Bot is still running.");
        }
    }

    @Scheduled(cron = "05 0 0 * * *", zone = "UTC")
    private void sendDailyReport() {

        String report = "Total Daily Profit: " + totalProfit + "%\n";

        Double fees = 0.15 * soldCoins.size();

        report += "Total Daily Profit After Fees: " + (totalProfit - fees) + "%\n\n";

        for (WinningCoin soldCoin : soldCoins) {
            report += "Symbol: " + soldCoin.getSymbol() + "\n";
            report += "Date / Time of Purchase (EST): " + soldCoin.getBuyDateAndTime() + "\n";
            report += "Trade Time (Minutes): " + soldCoin.getTimeInMinutesTrading() + "\n";
            report += "Buy Price: " + soldCoin.getBuyPrice() + "\n";
            report += "Sell Price: " + soldCoin.getSellPrice() + "\n";
            report += "Highest Price: " + soldCoin.getHighestPrice() + "\n";
            report += "Difference Sell Price / Highest Price: " + soldCoin.getMarginFromCurrentAndHighestPrice() + "%\n";
            report += "Profit: " + soldCoin.getProfit() + "%\n\n";
        }

        if (!testMode) {
            emailHandler.sendEmail("Daily Report", report);
        } else {
            emailHandler.sendEmail("Daily Report (Test Mode)", report);
        }

        totalProfit = 0.0;
        soldCoins.clear();
    }
}