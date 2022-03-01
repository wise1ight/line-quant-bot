package kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by kuvh on 2017-04-12.
 */
public class ApiCompleteBalanceMessage {

    @JsonProperty("available")
    private BigDecimal available;

    @JsonProperty("onOrders")
    private BigDecimal onOrders;

    @JsonProperty("btcValue")
    private BigDecimal btcValue;

    public BigDecimal getAvailable() {
        return available;
    }

    public void setAvailable(BigDecimal available) {
        this.available = available;
    }

    public BigDecimal getOnOrders() {
        return onOrders;
    }

    public void setOnOrders(BigDecimal onOrders) {
        this.onOrders = onOrders;
    }

    public BigDecimal getBtcValue() {
        return btcValue;
    }

    public void setBtcValue(BigDecimal btcValue) {
        this.btcValue = btcValue;
    }
}
