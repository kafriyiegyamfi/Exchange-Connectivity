package com.trade.utility;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trade.models.PendingOrder;
import org.springframework.web.reactive.function.client.WebClient;

public class Utility {
    public static <T> T convertToObject(String data, Class<T> type) {
        ObjectMapper objectMapper = new ObjectMapper();
        T t = null;
        try {
            t = objectMapper.readValue(data, type);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> String convertToString(T t) {
        ObjectMapper objectMapper = new ObjectMapper();
        String str = null;
        try {
            str = objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException ignore) {
        }
        return str;
    }

    public static PendingOrder[] requestOrderBook(String side, String product, String exchange) {
        if (side.equals("sell")) {
            return WebClient.builder()
                    .baseUrl(exchange)
                    .build()
                    .get()
                    .uri("/orderbook" + "/" + product + "/" + "buy")
                    .retrieve()
                    .bodyToMono(PendingOrder[].class)
                    .block();
        } else {
            return WebClient.builder()
                    .baseUrl("https://exchange.matraining.com")
                    .build()
                    .get()
                    .uri("/orderbook" + "/" + product + "/" + "sell")
                    .retrieve()
                    .bodyToMono(PendingOrder[].class)
                    .block();
        }
    }
}
