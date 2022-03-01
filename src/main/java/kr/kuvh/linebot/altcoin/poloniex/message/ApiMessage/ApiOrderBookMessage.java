package kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by kuvh on 2017-04-19.
 */
public class ApiOrderBookMessage {

    @JsonProperty("asks")
    private List<List<BigDecimal>> asks;

    @JsonProperty("bids")
    private List<List<BigDecimal>> bids;

    @JsonProperty("isFrozen")
    private String isFrozen;

    @JsonProperty("seq")
    private long seq;

    public List<List<BigDecimal>> getAsks() {
        return asks;
    }

    public void setAsks(List<List<BigDecimal>> asks) {
        this.asks = asks;
    }

    public List<List<BigDecimal>> getBids() {
        return bids;
    }

    public void setBids(List<List<BigDecimal>> bids) {
        this.bids = bids;
    }

    public String isFrozen() {
        return isFrozen;
    }

    public void setFrozen(String frozen) {
        isFrozen = frozen;
    }

    public long getSeq() {
        return seq;
    }

    public void setSeq(long seq) {
        this.seq = seq;
    }

}
