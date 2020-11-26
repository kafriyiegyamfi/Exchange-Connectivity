package com.trade.models;

import com.trade.utility.JedisConnection;
import com.trade.utility.ObjectSerializer;
import com.trade.utility.Utility;
import redis.clients.jedis.Jedis;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Base64;

public class MakeOrder implements Runnable{
    private final String QUEUE_NAME;
    private static final String MALLON_1 = "https://exchange.matraining.com";
    private static final String MALLON_2 = "https://exchange2.matraining.com";

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
            System.out.println("Serialized data: " + data);

            try{
                byte[] byteData = Base64.getDecoder().decode(data.getBytes());

                OrderBookRequest orderBookRequest = (OrderBookRequest) ObjectSerializer.unserialize(byteData);

                // TODO: 11/25/20 Remove before shipping
                System.out.println(orderBookRequest.toString());

                PendingOrder[] response = null;
                String product = orderBookRequest.product;

                String side = orderBookRequest.side.toLowerCase();

                response = Utility.requestOrderBook(side, product,QUEUE_NAME=="exchange1OrderRequest"?MALLON_1:MALLON_2);

                PendingOrder[] pendingOrders = response;

                if (pendingOrders == null) {
                    System.out.println("Pending orders null");
                }
                for (int i = 0; i < pendingOrders.length; i++) {
                    pendingOrders[i].exchange =  QUEUE_NAME=="exchange1OrderRequest"?"exchange1":"exchange2";
                    System.out.println(pendingOrders[i]);
                }
                jedis.lpush(orderBookRequest.id + "orderbook", (Base64.getEncoder().encodeToString(ObjectSerializer.serialize(pendingOrders))));
            }catch(IllegalArgumentException e){
                System.out.println("Exception message\n");
                System.out.println(e.getMessage());
            }
        }
    }
}
