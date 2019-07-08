package com.binance.handler;

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

    @Value("${trade.profitBeforeSelling}")
    private Double profitBeforeSelling;

    @Value("${trade.lossBeforeSelling}")
    private Double lossBeforeSelling;

    @Value("${trade.marginFromCurrentAndHighestPrice}")
    private Double marginFromCurrentAndHighestPrice;

    private Account account;

    private Order order;

    private Double quantity;

    private int holdCoinCount;

    public WinningCoin tradeCoin(WinningCoin winningCoin) {

        if (!winningCoin.isBought()) {
            buyCoin(winningCoin);
        } else if (winningCoin.isBought() && winningCoin.getProfitSinceBuyPrice() < profitBeforeSelling
                && winningCoin.getProfitSinceBuyPrice() > lossBeforeSelling) {
            holdCoin(winningCoin);
        } else if (winningCoin.isBought() && winningCoin.getProfitSinceBuyPrice() >= profitBeforeSelling
                || winningCoin.getProfitSinceBuyPrice() <= lossBeforeSelling) {
            if (winningCoin.getProfitSinceBuyPrice() >= lossBeforeSelling
                    && winningCoin.getMarginFromCurrentAndHighestPrice() >= marginFromCurrentAndHighestPrice) {
                holdCoin(winningCoin);
            } else {
                sellCoin(winningCoin);
            }
        }
        return winningCoin;
    }

    public void buyCoin(WinningCoin winningCoin) {

        holdCoinCount = 0;

        account = new Account();
        account = accountService.getAccountInfo();

        Double btcQuantity = 0.0;
        Double bnbQuantity = 0.0;

        for (Balance balance : account.getBalances()) {
            if (balance.getAsset().equals("BTC")) {
                btcQuantity = balance.getFree();
            }
            if (balance.getAsset().equals("BNB")) {
                bnbQuantity = balance.getFree();
            }
        }

        quantity = btcQuantity - bnbQuantity;
        quantity = (quantity / winningCoin.getCurrentPrice()) * .95;

        if (quantity <= 1.99) {
            quantity = Math.round(quantity * 100.0) / 100.0;
        } else {
            quantity = Double.valueOf(Math.round(quantity));
            if (quantity > 2.0) {
                quantity = quantity - 1;
            }
        }

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        LOGGER.info("UTC Time is: " + dateFormat.format(date));
        LOGGER.info("******************** BUYING COIN ********************");
        LOGGER.info(winningCoin.getSymbol() + " - " + winningCoin.toString());

        order = new Order();
        order = orderService.postBuyOrder(winningCoin, quantity);

        int count = 0;
        Double avgPrice = 0.0;

        for (Fill fill : order.getFills()) {
            avgPrice = avgPrice + fill.getPrice();
            count++;
        }

        avgPrice = avgPrice / count;

        winningCoin.setBuyPrice(avgPrice);
        winningCoin.setProfitSinceBuyPrice();
        winningCoin.setBought(true);
    }

    public void holdCoin(WinningCoin winningCoin) {

        holdCoinCount++;
        winningCoin.setProfitSinceBuyPrice();

        if (holdCoinCount >= 720) {
            holdCoinCount = 0;

            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

            LOGGER.info("UTC Time is: " + dateFormat.format(date));
            LOGGER.info("******************** HOLDING COIN *******************");
            LOGGER.info(winningCoin.getSymbol() + " - " + winningCoin.toString());
        }
    }

    public void sellCoin(WinningCoin winningCoin) {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        LOGGER.info("UTC Time is: " + dateFormat.format(date));
        LOGGER.info("******************* SELLING COIN ********************");
        LOGGER.info(winningCoin.getSymbol() + " - " + winningCoin.toString());

        order = new Order();
        order = orderService.postSellOrder(winningCoin, quantity);

        int count = 0;
        Double avgPrice = 0.0;

        for (Fill fill : order.getFills()) {
            avgPrice = avgPrice + fill.getPrice();
            count++;
        }

        avgPrice = avgPrice / count;

        winningCoin.setSellPrice(avgPrice);
        winningCoin.setProfit();
        winningCoin.setSold(true);
    }
}