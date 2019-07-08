package com.binance.cryptoBot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ComponentScan(basePackages = "com.binance")
public class CryptoBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(CryptoBotApplication.class, args);
	}
}
