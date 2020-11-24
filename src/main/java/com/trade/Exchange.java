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
    //	EXCHANGE 1
    private static final int PORT = 8090;
    private static final String MALLON_API_KEY = "40c97d23-827c-4ab8-ab55-862ec3982ede";
    private static final String MALLON_1 = "https://exchange.matraining.com";
    private static final String MALLON_2 = "https://exchange2.matraining.com";
    private static Jedis jedis = null;

    public static void main(String[] args) {
        SpringApplication.run(Exchange.class, args);

        Thread makeOrderToExchangeOne = new Thread(new MakeOrder("exchange1OrderRequest"));
        makeOrderToExchangeOne.start();

        Thread placeOrderToExchangeOne = new Thread(new PlaceOrder());
        placeOrderToExchangeOne.start();

    }

}
