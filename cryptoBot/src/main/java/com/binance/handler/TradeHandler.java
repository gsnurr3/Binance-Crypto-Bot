package com.binance.handler;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.binance.model.Account;
import com.binance.model.Balance;
import com.binance.model.Fill;
import com.binance.model.Order;
import com.binance.model.WinningCoin;
import com.binance.service.AccountService;
import com.binance.service.OrderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

/**
 * TradeService
 */
@Component
public class TradeHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(TradeHandler.class);

    @Autowired
    private AccountService accountService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private EmailHandler emailHandler;

    @Value("${trade.max.time}")
    private Double maxTradeTime;

    @Value("${trade.profitBeforeSelling}")
    private Double profitBeforeSelling;

    @Value("${trade.lossBeforeSelling}")
    private Double lossBeforeSelling;

    @Value("${trade.marginFromCurrentAndHighestPrice}")
    private Double marginFromCurrentAndHighestPrice;

    @Value("${trade.test.mode}")
    private Boolean testMode;

    private Account account;

    private Order order;

    private Double quantity;

    private int maxTradeTimeCounter;

    private int holdCoinCount;

    private Double diminishingMargin;

    public WinningCoin tradeCoin(WinningCoin winningCoin)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException {

        diminishingMargin = setDiminishingMargin(winningCoin);

        if (!winningCoin.isBought()) {
            buyCoin(winningCoin);
        } else if (winningCoin.isBought() && maxTradeTimeCounter / 720 >= maxTradeTime) {
            sellCoin(winningCoin);
        } else if (winningCoin.isBought() && winningCoin.getProfitSinceBuyPrice() < profitBeforeSelling
                && winningCoin.getProfitSinceBuyPrice() > lossBeforeSelling) {
            holdCoin(winningCoin);
        } else if (winningCoin.isBought() && winningCoin.getProfitSinceBuyPrice() >= profitBeforeSelling
                || winningCoin.getProfitSinceBuyPrice() <= lossBeforeSelling) {
            if (winningCoin.getProfitSinceBuyPrice() >= lossBeforeSelling && winningCoin
                    .getMarginFromCurrentAndHighestPrice() >= (marginFromCurrentAndHighestPrice + diminishingMargin)) {
                holdCoin(winningCoin);
            } else {
                sellCoin(winningCoin);
            }
        }
        return winningCoin;
    }

    private void buyCoin(WinningCoin winningCoin)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException {

        diminishingMargin = 0.0;
        maxTradeTimeCounter = 0;
        holdCoinCount = 0;

        if (!testMode) {
            account = new Account();
            account = accountService.getAccountInfo();

            for (Balance balance : account.getBalances()) {
                if (balance.getAsset().equals("BTC")) {
                    quantity = (balance.getFree() / winningCoin.getCurrentPrice()) * .90;
                }
            }

            if (quantity <= 1.99) {
                quantity = Math.round(quantity * 100.0) / 100.0;
            } else {
                quantity = Double.valueOf(Math.round(quantity));
                if (quantity > 2.0) {
                    quantity = quantity - 1;
                }
            }

            order = new Order();
            order = orderService.postBuyOrder(winningCoin, quantity);

            Double totalQuantity = 0.0;
            Double avgWeightedPrice = 0.0;

            if (order.getFills().size() > 1) {
                for (Fill fill : order.getFills()) {
                    avgWeightedPrice = avgWeightedPrice + (fill.getPrice() * fill.getQty());
                    totalQuantity = totalQuantity + fill.getQty();
                }

                avgWeightedPrice = avgWeightedPrice / totalQuantity;
            } else {
                avgWeightedPrice = order.getFills().get(0).getPrice();
            }

            winningCoin.setBuyPrice(avgWeightedPrice);
        } else {
            winningCoin.setBuyPrice(winningCoin.getCurrentPrice());
        }

        winningCoin.setProfitSinceBuyPrice();
        winningCoin.setBought(true);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        LOGGER.info("UTC Time is: " + dateFormat.format(date));
        LOGGER.info("******************** BUYING COIN ********************");
        LOGGER.info(winningCoin.getSymbol() + " - " + winningCoin.toString());
    }

    private void holdCoin(WinningCoin winningCoin) {

        maxTradeTimeCounter++;
        holdCoinCount++;
        winningCoin.setProfitSinceBuyPrice();

        if (holdCoinCount >= 360) {
            holdCoinCount = 0;

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            LOGGER.info("UTC Time is: " + dateFormat.format(date));
            LOGGER.info("******************** HOLDING COIN *******************");
            LOGGER.info(winningCoin.getSymbol() + " - " + winningCoin.toString());
        }
    }

    private void sellCoin(WinningCoin winningCoin) {

        if (!testMode) {
            order = new Order();

            try {
                order = orderService.postSellOrder(winningCoin, quantity);
            } catch (IOException | NullPointerException e) {
                LOGGER.error(e.toString());
                emailHandler.sendEmail("Error", e.toString());

                try {
                    Thread.sleep(5000);
                    sellCoin(winningCoin);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }

            Double totalQuantity = 0.0;
            Double avgWeightedPrice = 0.0;

            if (order.getFills().size() > 1) {
                for (Fill fill : order.getFills()) {
                    avgWeightedPrice = avgWeightedPrice + (fill.getPrice() * fill.getQty());
                    totalQuantity = totalQuantity + fill.getQty();
                }

                avgWeightedPrice = avgWeightedPrice / totalQuantity;
            } else {
                avgWeightedPrice = order.getFills().get(0).getPrice();
            }

            winningCoin.setSellPrice(avgWeightedPrice);
        } else {
            winningCoin.setSellPrice(winningCoin.getCurrentPrice());
        }

        winningCoin.setProfitSinceBuyPrice();
        winningCoin.setProfit();
        winningCoin.setSold(true);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        LOGGER.info("UTC Time is: " + dateFormat.format(date));
        LOGGER.info("******************* SELLING COIN ********************");
        LOGGER.info(winningCoin.getSymbol() + " - " + winningCoin.toString());
    }

    private Double setDiminishingMargin(WinningCoin winningCoin) {

        if (winningCoin.isBought()) {
            if (winningCoin.getProfitSinceBuyPrice() >= 1.00 && diminishingMargin < 0.50) {
                diminishingMargin = 0.50;
            }
            if (winningCoin.getProfitSinceBuyPrice() >= 1.50 && diminishingMargin < 1.00) {
                diminishingMargin = 1.00;
            }
            if (winningCoin.getProfitSinceBuyPrice() >= 2.00 && diminishingMargin < 1.50) {
                diminishingMargin = 1.50;
            }
            if (winningCoin.getProfitSinceBuyPrice() >= 6.00) {
                diminishingMargin = 0.0;
            }
        }

        return diminishingMargin;
    }
}