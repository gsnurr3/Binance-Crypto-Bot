package com.binance.dto;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Scanner;

import com.binance.api.KlinesAPI;
import com.binance.helper.RestTemplateHelper;
import com.binance.model.CandleStick_1H;
import com.binance.model.CandleStick_24H;
import com.binance.model.Coin;

import org.apache.http.conn.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

/**
 * KlinesDTO
 */
@Component
public class KlinesDTO {

    private static final Logger LOGGER = LoggerFactory.getLogger(KlinesDTO.class);

    @Value("${klines.hour.limit}")
    private int hourLimit;

    @Value("${klines.hour.interval}")
    private String hourInterval;

    @Value("${klines.day.limit}")
    private int dayLimit;

    @Value("${klines.day.interval}")
    private String dayInterval;

    @Autowired
    private KlinesAPI klinesAPI;

    @Autowired
    private RestTemplateHelper restTemplateHelper;

    public List<Coin> getAllCandleSticks_1H(List<Coin> coins)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        LOGGER.info("Getting 1 hour candlesticks for past " + hourLimit + " hours...");

        for (Coin coin : coins) {

            klinesAPI.ListSymbol(coin.getSymbol());

            String queryString = "?symbol=" + klinesAPI.getSymbol() + "&interval=" + hourInterval + "&limit="
                    + hourLimit;

            ResponseEntity<String> responseEntity = null;

            responseEntity = restTemplateHelper.getResponseEntityString(klinesAPI.getKLINES_ENDPOINT() + queryString);

            String result = null;

            result = responseEntity.getBody();

            // Code from here down needs to be refactored
            result = result.replace("[", "");
            result = result.replace("]", "");
            result = result.replace("[{", "");
            result = result.replace("}]", "}]");
            result = result.replace("\"", "");
            result = result.replace(",", "\n");

            Scanner scanner = new Scanner(result);

            CandleStick_1H candleStick_1H = null;
            coin.getCandleSticks_1H().clear();

            while (scanner.hasNext()) {

                candleStick_1H = new CandleStick_1H();

                candleStick_1H.setOpenTime(Long.parseLong(scanner.next()));
                candleStick_1H.setOpenPrice(Double.parseDouble(scanner.next()));
                candleStick_1H.setHighPrice(Double.parseDouble(scanner.next()));
                candleStick_1H.setLowPrice(Double.parseDouble(scanner.next()));
                candleStick_1H.setClosePrice(Double.parseDouble(scanner.next()));
                candleStick_1H.setVolume(Double.parseDouble(scanner.next()));
                candleStick_1H.setCloseTime(Long.parseLong(scanner.next()));
                candleStick_1H.setQuoteAssetVolume(Double.parseDouble(scanner.next()));
                candleStick_1H.setNumberOfTrades(Double.parseDouble(scanner.next()));
                candleStick_1H.setTakerBuyBaseAssetVolume(Double.parseDouble(scanner.next()));
                candleStick_1H.setTakerBuyQuoteAssetVolume(Double.parseDouble(scanner.next()));
                candleStick_1H.setIgnore(Double.parseDouble(scanner.next()));

                coin.addCandleSticks_1H(candleStick_1H);
            }
            scanner.close();
        }

        return coins;
    }

    public List<Coin> getAllCandleSticks_24H(List<Coin> coins)
            throws ResourceAccessException, SocketTimeoutException, IOException, NullPointerException, ConnectTimeoutException {

        LOGGER.info("Getting 24 hour candlesticks for past " + dayLimit + " days...");

        for (Coin coin : coins) {

            klinesAPI.ListSymbol(coin.getSymbol());

            String queryString = "?symbol=" + klinesAPI.getSymbol() + "&interval=" + dayInterval + "&limit=" + dayLimit;

            ResponseEntity<String> responseEntity = null;

            responseEntity = restTemplateHelper.getResponseEntityString(klinesAPI.getKLINES_ENDPOINT() + queryString);

            String result = null;

            result = responseEntity.getBody();

            // Code from here down needs to be refactored
            result = result.replace("[", "");
            result = result.replace("]", "");
            result = result.replace("[{", "");
            result = result.replace("}]", "}]");
            result = result.replace("\"", "");
            result = result.replace(",", "\n");

            Scanner scanner = new Scanner(result);

            CandleStick_24H candleStick_24H = null;
            coin.getCandleSticks_24H().clear();

            while (scanner.hasNext()) {

                candleStick_24H = new CandleStick_24H();

                candleStick_24H.setOpenTime(Long.parseLong(scanner.next()));
                candleStick_24H.setOpenPrice(Double.parseDouble(scanner.next()));
                candleStick_24H.setHighPrice(Double.parseDouble(scanner.next()));
                candleStick_24H.setLowPrice(Double.parseDouble(scanner.next()));
                candleStick_24H.setClosePrice(Double.parseDouble(scanner.next()));
                candleStick_24H.setVolume(Double.parseDouble(scanner.next()));
                candleStick_24H.setCloseTime(Long.parseLong(scanner.next()));
                candleStick_24H.setQuoteAssetVolume(Double.parseDouble(scanner.next()));
                candleStick_24H.setNumberOfTrades(Double.parseDouble(scanner.next()));
                candleStick_24H.setTakerBuyBaseAssetVolume(Double.parseDouble(scanner.next()));
                candleStick_24H.setTakerBuyQuoteAssetVolume(Double.parseDouble(scanner.next()));
                candleStick_24H.setIgnore(Double.parseDouble(scanner.next()));

                coin.addCandleSticks_24H(candleStick_24H);
            }
            scanner.close();
        }

        return coins;
    }
}