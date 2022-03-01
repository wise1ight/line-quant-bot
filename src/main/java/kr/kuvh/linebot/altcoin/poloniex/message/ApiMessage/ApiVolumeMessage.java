package kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by kuvh on 2017-04-15.
 */
public class ApiVolumeMessage {

    private Map<String, Map<String, BigDecimal>> properties = new HashMap<>();

    @JsonProperty("totalBTC")
    private BigDecimal totalBTC;

    @JsonProperty("totalETH")
    private BigDecimal totalETH;

    @JsonProperty("totalUSDT")
    private BigDecimal totalUSDT;

    @JsonProperty("totalXMR")
    private BigDecimal totalXMR;

    @JsonProperty("totalXUSD")
    private BigDecimal totalXUSD;

    public BigDecimal getTotalBTC() {
        return totalBTC;
    }

    public void setTotalBTC(BigDecimal totalBTC) {
        this.totalBTC = totalBTC;
    }

    public BigDecimal getTotalETH() {
        return totalETH;
    }

    public void setTotalETH(BigDecimal totalETH) {
        this.totalETH = totalETH;
    }

    public BigDecimal getTotalUSDT() {
        return totalUSDT;
    }

    public void setTotalUSDT(BigDecimal totalUSDT) {
        this.totalUSDT = totalUSDT;
    }

    public BigDecimal getTotalXMR() {
        return totalXMR;
    }

    public void setTotalXMR(BigDecimal totalXMR) {
        this.totalXMR = totalXMR;
    }

    public BigDecimal getTotalXUSD() {
        return totalXUSD;
    }

    public void setTotalXUSD(BigDecimal totalXUSD) {
        this.totalXUSD = totalXUSD;
    }

    @JsonAnySetter
    public void add(String key, Map<String, BigDecimal> value) {
        properties.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Map<String, BigDecimal>> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "Bean{" +
                "properties=" + properties +
                ", totalBTC=" + totalBTC +
                ", totalETH=" + totalETH +
                ", totalUSDT=" + totalUSDT +
                ", totalXMR=" + totalXMR +
                ", totalXUSD=" + totalXUSD +
                '}';
    }

}
