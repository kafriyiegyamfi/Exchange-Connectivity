package com.trade;

import com.trade.models.Order;
import com.trade.models.OrderBookRequest;
import com.trade.models.PendingOrder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import redis.clients.jedis.Jedis;

@SpringBootApplication
public class ExchangeconnectivityApplication {
//	EXCHANGE 1
	public static void main(String[] args) {
		SpringApplication.run(ExchangeconnectivityApplication.class, args);

		new Thread(new Runnable() {
			Jedis jedis = new Jedis();
			@Override
			public void run() {
				while (true){
					String data = jedis.rpop("exchange1-orderrequest");
					if(data == null) continue;
					System.out.println(data);
					OrderBookRequest orderBookRequest = Utility.convertToObject(data,OrderBookRequest.class);


					PendingOrder[] response = null;
					String product = orderBookRequest.product;

					String side = orderBookRequest.side.toLowerCase();

					response=Utility.requestOrderBook(side,product);

					PendingOrder[] pendingOrders = response;
					if(pendingOrders == null) {
						System.out.println("Pending orders null");
					}
					for (int i = 0 ; i< pendingOrders.length;i++) {

						pendingOrders[i].exchange = "exchange1";
					}
					jedis.lpush(orderBookRequest.id + "orderbook",Utility.convertToString(pendingOrders));
				}
			}
		}).start();


//		MAKE ORDER
		new Thread(new Runnable() {
			Jedis jedis = new Jedis();
			String key = "40c97d23-827c-4ab8-ab55-862ec3982ede";
			WebClient webClient = WebClient.create("https://exchange.matraining.com");
			@Override
			public void run() {
				while (true){

					String data = jedis.rpop("makeorderexchange1");

					if(data == null) continue;

					System.out.println("exchange1");
					String order2 = Utility.convertToString(data);
					System.out.println(order2);

					Order order = Utility.convertToObject(data, Order.class);

					String orderId = webClient.post().uri("/"+key+"/order")
							.body(Mono.just(order), Order.class)
							.retrieve()
							.bodyToMono(String.class)
							.block();

					System.out.println("Order placed successfully, orderId: " +order);

				}
			}
		}).start();
	}
}
