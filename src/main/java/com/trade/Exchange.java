package com.trade;

import com.trade.models.*;
import com.trade.utility.Utility;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootApplication
public class Exchange {
    private static final String MALLON_API_KEY_1 = "40c97d23-827c-4ab8-ab55-862ec3982ede";
    private static final String MALLON_API_KEY_2 = "787a425a-9a94-4c1c-82df-c0a0d8239cf4";
    private static final String MALLON_1 = "https://exchange.matraining.com";
    private static final String MALLON_2 = "https://exchange2.matraining.com";

    public static void main(String[] args) {
        SpringApplication.run(Exchange.class, args);

        Thread makeOrderToExchangeOne = new Thread(new MakeOrder("exchange1OrderRequest"));
        makeOrderToExchangeOne.start();

        Thread makeOrderToExchangeTwo = new Thread(new MakeOrder("exchange2OrderRequest"));
        makeOrderToExchangeTwo.start();

        Thread placeOrderToExchangeOne = new Thread(new PlaceOrder("makeOrderExchange1", MALLON_1, MALLON_API_KEY_1));
        placeOrderToExchangeOne.start();

        Thread placeOrderToExchangeTwo = new Thread(new PlaceOrder("makeOrderExchange2", MALLON_2, MALLON_API_KEY_2));
        placeOrderToExchangeTwo.start();
    }
}
