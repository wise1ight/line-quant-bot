package kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by kuvh on 2017-04-19.
 */
public class ApiResultingTradesMessage {
    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("date")
    private String date;

    @JsonProperty("rate")
    private BigDecimal rate;

    @JsonProperty("total")
    private BigDecimal total;

    @JsonProperty("tradeID")
    private long tradeID;

    @JsonProperty("type")
    private String type;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
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

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public long getTradeID() {
        return tradeID;
    }

    public void setTradeID(long tradeID) {
        this.tradeID = tradeID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
