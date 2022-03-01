package kr.kuvh.linebot.altcoin.api.coinone;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by kuvh on 2017-04-08.
 */
public class CompleteOrder {

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("price")
    private int price;

    @JsonProperty("qty")
    private double qty;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }
}
