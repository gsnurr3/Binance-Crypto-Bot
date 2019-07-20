package com.binance.service;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;

import com.binance.dto.KlinesDTO;
import com.binance.handler.EmailHandler;
import com.binance.model.CandleStick_1H;
import com.binance.model.CandleStick_24H;
import com.binance.model.Coin;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

/**
 * CandleStick_1H_Service
 */
@Service
public class KlinesService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KlinesService.class);

    @Autowired
    private KlinesDTO klinesDTO;

    @Autowired
    private EmailHandler emailHandler;

    public List<Coin> getAllCandleSticks_1H(List<Coin> coins) {

        try {
            coins = klinesDTO.getAllCandleSticks_1H(coins);
        } catch (ResourceAccessException | SocketTimeoutException | ConnectTimeoutException | NullPointerException e1) {

            LOGGER.error(e1.toString());
            emailHandler.sendEmail("Error", e1.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            this.getAllCandleSticks_1H(coins);
        } catch (IOException e3) {
            LOGGER.error(e3.toString());
            emailHandler.sendEmail("Error", e3.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e4) {
                e4.printStackTrace();
            }
            this.getAllCandleSticks_1H(coins);
        }

        for (Coin coin : coins) {
            for (CandleStick_1H candleStick_1H : coin.getCandleSticks_1H()) {
                if (candleStick_1H.getClosePrice() > candleStick_1H.getOpenPrice()) {
                    Double endOfHourGain = 0.0;

                    endOfHourGain = ((candleStick_1H.getClosePrice() - candleStick_1H.getOpenPrice())
                            / candleStick_1H.getOpenPrice()) * 100;
                    candleStick_1H.setEndOfCandleStickGain(endOfHourGain);
                }
                if (candleStick_1H.getClosePrice() < candleStick_1H.getOpenPrice()) {
                    Double endOfHourLoss = 0.0;

                    endOfHourLoss = ((candleStick_1H.getLowPrice() - candleStick_1H.getOpenPrice())
                            / candleStick_1H.getOpenPrice()) * 100;
                    candleStick_1H.setEndOfCandleStickLoss(endOfHourLoss);
                }
            }
        }

        return coins;
    }

    public List<Coin> getAllCandleSticks_24H(List<Coin> coins) {

        try {
            coins = klinesDTO.getAllCandleSticks_24H(coins);
        } catch (ResourceAccessException | SocketTimeoutException | ConnectTimeoutException | NullPointerException e1) {

            LOGGER.error(e1.toString());
            emailHandler.sendEmail("Error", e1.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e2) {
                e2.printStackTrace();
            }
            this.getAllCandleSticks_24H(coins);
        } catch (IOException e3) {
            LOGGER.error(e3.toString());
            emailHandler.sendEmail("Error", e3.toString());

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e4) {
                e4.printStackTrace();
            }
            this.getAllCandleSticks_24H(coins);
        }

        for (Coin coin : coins) {
            for (CandleStick_24H candleStick_24H : coin.getCandleSticks_24H()) {
                if (candleStick_24H.getClosePrice() > candleStick_24H.getOpenPrice()) {
                    Double endOfDayGain = 0.0;

                    endOfDayGain = ((candleStick_24H.getClosePrice() - candleStick_24H.getOpenPrice())
                            / candleStick_24H.getOpenPrice()) * 100;
                    candleStick_24H.setEndOfCandleStickGain(endOfDayGain);
                }
                if (candleStick_24H.getClosePrice() < candleStick_24H.getOpenPrice()) {
                    Double endOfDayLoss = 0.0;

                    endOfDayLoss = ((candleStick_24H.getLowPrice() - candleStick_24H.getOpenPrice())
                            / candleStick_24H.getOpenPrice()) * 100;
                    candleStick_24H.setEndOfCandleStickLoss(endOfDayLoss);
                }
            }
        }

        return coins;
    }
}