package kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by kuvh on 2017-04-19.
 */
public class ApiCurrencyPairMessage {
    @JsonProperty("orderNumber")
    private long orderNumber;

    @JsonProperty("resultingTrades")
    private List<ApiResultingTradesMessage> resultingTrades;

    public long getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(long orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<ApiResultingTradesMessage> getResultingTrades() {
        return resultingTrades;
    }

    public void setResultingTrades(List<ApiResultingTradesMessage> resultingTrades) {
        this.resultingTrades = resultingTrades;
    }
}
