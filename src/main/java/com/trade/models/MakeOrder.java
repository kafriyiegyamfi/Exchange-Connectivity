package com.trade.models;

import com.trade.utility.JedisConnection;
import com.trade.utility.Utility;
import redis.clients.jedis.Jedis;

import java.net.URISyntaxException;
import java.util.Arrays;

public class MakeOrder implements Runnable{

    private final String QUEUE_NAME;

    public MakeOrder(String name) {
        this.QUEUE_NAME = name;
    }

    @Override
    public void run() {
        while (true) {
            Jedis jedis = null;
            try {
                jedis = (new JedisConnection()).getConnection();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            String data = jedis.rpop(this.QUEUE_NAME);
            if (data == null) continue;
            System.out.println(data);
            OrderBookRequest orderBookRequest = Utility.convertToObject(data, OrderBookRequest.class);


            PendingOrder[] response = null;
            String product = orderBookRequest.product;

            String side = orderBookRequest.side.toLowerCase();

            response = Utility.requestOrderBook(side, product);

            PendingOrder[] pendingOrders = response;
            Arrays.stream(pendingOrders).forEach(System.out::println);

            if (pendingOrders == null) {
                System.out.println("Pending orders null");
            }
            for (int i = 0; i < pendingOrders.length; i++) {

                pendingOrders[i].exchange = "exchange1";
            }
            jedis.lpush(orderBookRequest.id + "orderbook", Utility.convertToString(pendingOrders));
        }
    }
}
