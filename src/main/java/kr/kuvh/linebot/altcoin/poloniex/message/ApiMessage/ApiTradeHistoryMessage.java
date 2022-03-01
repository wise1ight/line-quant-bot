package kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by kuvh on 2017-04-28.
 */
public class ApiTradeHistoryMessage {

    @JsonProperty("globalTradeID")
    private long globalTradeID;

    @JsonProperty("tradeID")
    private long tradeID;

    @JsonProperty("date")
    private String date;

    @JsonProperty("rate")
    private BigDecimal rate;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("fee")
    private BigDecimal fee;

    @JsonProperty("orderNumber")
    private long orderNumber;

    @JsonProperty("type")
    private String sell;

    @JsonProperty("category")
    private String exchange;

    public long getGlobalTradeID() {
        return globalTradeID;
    }

    public void setGlobalTradeID(long globalTradeID) {
        this.globalTradeID = globalTradeID;
    }

    public long getTradeID() {
        return tradeID;
    }

    public void setTradeID(long tradeID) {
        this.tradeID = tradeID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getSell() {
        return sell;
    }

    public void setSell(String sell) {
        this.sell = sell;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }
}
