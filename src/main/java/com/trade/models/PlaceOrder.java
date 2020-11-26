package com.trade.models;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import com.trade.utility.JedisConnection;
import com.trade.utility.ObjectSerializer;
import com.trade.utility.Utility;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;

import java.net.URISyntaxException;

public class PlaceOrder implements Runnable{
    private final String MALLON_API_KEY;
    private final String MALLON_URL;
    private final String EXCHANGE;

    public PlaceOrder(String MALLON_API_KEY, String MALLON_URL, String EXCHANGE) {
        this.MALLON_API_KEY = MALLON_API_KEY;
        this.MALLON_URL = MALLON_URL;
        this.EXCHANGE = EXCHANGE;
    }

    @Override
    public void run() {
        Jedis jedis = null;
        while (true) {
            try {
                jedis = (new JedisConnection()).getConnection();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String data = jedis.rpop(this.EXCHANGE);

            if (data == null) continue;
            System.out.println("placeorder"+ data);
            byte[] byteData = java.util.Base64.getDecoder().decode(data.getBytes());
            PartialOrder partialOrder = (PartialOrder) ObjectSerializer.unserialize(byteData);
            // TODO: 11/25/20 Conform order object to form that can be passed to the exchange.
 //          RestTemplate restTemplate = new RestTemplate();
//            System.out.println(restTemplate.postForObject(MALLON_1 + "/" + MALLON_API_KEY + "/order", partialOrder.toString(), String.class));


            WebClient webClient = WebClient.create(this.MALLON_URL);
            String orderId = webClient.post().uri("/" + MALLON_API_KEY + "/order")
                    .body(Mono.just(partialOrder), PartialOrder.class)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

         System.out.println("PartialOrder placed successfully, orderId: " + orderId);
//         jedis.del("data");
        }
    }
}
