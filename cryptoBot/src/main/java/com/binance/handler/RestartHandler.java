package com.binance.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.restart.RestartEndpoint;
import org.springframework.stereotype.Component;

/**
 * RestartHandler
 */
@Component
public class RestartHandler {

    @Autowired
    private RestartEndpoint restartEndpoint;
     
    public void restartApp() {
        restartEndpoint.restart();
    }
}