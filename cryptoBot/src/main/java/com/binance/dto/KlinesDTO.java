package com.binance.dto;

import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Scanner;

import com.binance.api.KlinesAPI;
import com.binance.handler.EmailHandler;
import com.binance.helper.RestTemplateHelper;
import com.binance.model.CandleStick_24H;
import com.binance.model.Coin;

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

    @Value("${klines.limit}")
    private int limit;

    @Autowired
    private KlinesAPI klinesAPI;

    @Autowired
    private RestTemplateHelper restTemplateHelper;

    @Autowired
    private EmailHandler emailhandler;

    public List<Coin> getAllCandleSticks_24H(List<Coin> coins) {

        LOGGER.info("Getting 24 hour candlesticks for past " + limit + " days...");

        for (Coin coin : coins) {

            klinesAPI.ListSymbol(coin.getSymbol());

            String queryString = "?symbol=" + klinesAPI.getSymbol() + "&interval=" + klinesAPI.getInterval() + "&limit="
                    + klinesAPI.getLimit();

            ResponseEntity<String> responseEntity = null;

            try {
                responseEntity = restTemplateHelper
                        .getResponseEntityString(klinesAPI.getKLINES_ENDPOINT() + queryString);
            } catch (ResourceAccessException | SocketTimeoutException e) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e1) {
                    LOGGER.error(e.getMessage(), e1);
                } catch (Exception e2) {
                    emailhandler.sendEmail("Error", e2.toString());
                }
                getAllCandleSticks_24H(coins);
            } catch (Exception e3) {
                emailhandler.sendEmail("Error", e3.toString());
            }

            String result = responseEntity.getBody();

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

            // LOGGER.info(coin.toString());
        }

        return coins;
    }
}