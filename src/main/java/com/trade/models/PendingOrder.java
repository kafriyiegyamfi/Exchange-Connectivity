package com.trade.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PendingOrder {
    public String product;
    public int quantity;
    public double price;
    public String side;
    public int cumulatitiveQuantity;
    public String exchange;

    public PendingOrder() {
    }

    public PendingOrder(String product, int quantity, double price, String side, int cumulatitiveQuantity, String exchange) {
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.side = side;
        this.cumulatitiveQuantity = cumulatitiveQuantity;
        this.exchange = exchange;
    }

    @Override
    public String toString() {
        return "PendingOrder{" +
                "product='" + product + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", side='" + side + '\'' +
                ", cumulatitiveQuantity=" + cumulatitiveQuantity +
                ", exchange='" + exchange + '\'' +
                '}';
    };
    }

