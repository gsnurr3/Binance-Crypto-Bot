package com.binance.api;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * BaseAPI
 */
@Component
public abstract class BaseAPI {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseAPI.class);

    private final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
    private final String BASE_URL = "https://api.binance.com";

    @Value("${binance.account.apiKey}")
    private String apiKey;

    @Value("${binance.account.secretKey}")
    private String secretKey;

    @Value("${account.recvWindow}")
    private long recvWindow;

    private long timestamp;

    public String getBASE_URL() {
        return BASE_URL;
    }

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public long getRecvWindow() {
        return recvWindow;
    }

    public long getTimestamp() {

        Date date = new Date();
        timestamp = date.getTime();

        return timestamp;
    }

    public String getHmac256Signature(String queryString) {

        String signature = null;

        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(this.getSecretKey().getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            signature = this.bytesToHex(sha256_HMAC.doFinal(queryString.getBytes()));

        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            LOGGER.error(e.getMessage(), e);
        }

        return signature;
    }

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars);
    }
}