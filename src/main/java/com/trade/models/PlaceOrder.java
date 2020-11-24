package com.trade.models;

import com.trade.utility.JedisConnection;
import com.trade.utility.Utility;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;

import java.net.URISyntaxException;

public class PlaceOrder implements Runnable{
    private static final String MALLON_API_KEY = "40c97d23-827c-4ab8-ab55-862ec3982ede";
    private static final String MALLON_1 = "https://exchange.matraining.com";

    @Override
    public void run() {
        while (true) {
            Jedis jedis = null;
            try {
                jedis = (new JedisConnection()).getConnection();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String data = jedis.rpop("makeOrderexchange1");
            if (data == null) continue;

            System.out.println("exchange1");
            String order2 = Utility.convertToString(data);
            System.out.println(order2);

            Order order = Utility.convertToObject(data, Order.class);
            WebClient webClient = WebClient.create(MALLON_1);
            String orderId = webClient.post().uri("/" + MALLON_API_KEY + "/order")
                    .body(Mono.just(order), Order.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            System.out.println("Order placed successfully, orderId: " + orderId);

        }
    }
}
