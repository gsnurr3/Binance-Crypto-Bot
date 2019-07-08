package com.binance.helper;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplateHelper
 */
@Component
public class RestTemplateHelper {

    public ResponseEntity<String> getResponseEntityString(String url) throws SocketTimeoutException {

        URI uri = null;

        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

        return restTemplate.getForEntity(uri, String.class);
    }

    public ResponseEntity<String> getResponseEntitySHA256String(String url, String apiKey)
            throws SocketTimeoutException {

        URI uri = null;

        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ArrayList<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-MBX-APIKEY", apiKey);

        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

        return restTemplate.exchange(uri, HttpMethod.GET, request, new ParameterizedTypeReference<String>() {
        });
    }

    public ResponseEntity<String> postResponseEntitySHA256String(String url, String apiKey)
            throws SocketTimeoutException {

        URI uri = null;

        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        ArrayList<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(mediaTypes);
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-MBX-APIKEY", apiKey);

        HttpEntity<String> request = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate(getClientHttpRequestFactory());

        ResponseEntity<String> response = restTemplate.postForEntity(uri, request, String.class);

        return response;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }
}