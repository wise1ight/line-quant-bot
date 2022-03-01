package kr.kuvh.linebot.altcoin.api.coinone;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by kuvh on 2017-04-08.
 */
public class TradesApiMessage {

    @JsonProperty("errorCode")
    private int errorCode;

    @JsonProperty("timestamp")
    private long timestamp;

    @JsonProperty("completeOrders")
    private List<CompleteOrder> completeOrders;

    @JsonProperty("result")
    private String result;

    @JsonProperty("currency")
    private String currency;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public List<CompleteOrder> getCompleteOrders() {
        return completeOrders;
    }

    public void setCompleteOrders(List<CompleteOrder> completeOrders) {
        this.completeOrders = completeOrders;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
