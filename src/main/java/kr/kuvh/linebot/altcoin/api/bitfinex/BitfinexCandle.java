package kr.kuvh.linebot.altcoin.api.bitfinex;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import eu.verdelhan.ta4j.Decimal;

@JsonFormat(shape=JsonFormat.Shape.ARRAY)
@JsonPropertyOrder({"mts", "open", "close", "high", "low", "volume"})
public class BitfinexCandle {
    private long mts;
    private Decimal open;
    private Decimal close;
    private Decimal high;
    private Decimal low;
    private Decimal volume;

    public long getMts() {
        return mts;
    }

    public void setMts(long mts) {
        this.mts = mts;
    }

    public Decimal getOpen() {
        return open;
    }

    public void setOpen(Decimal open) {
        this.open = open;
    }

    public Decimal getClose() {
        return close;
    }

    public void setClose(Decimal close) {
        this.close = close;
    }

    public Decimal getHigh() {
        return high;
    }

    public void setHigh(Decimal high) {
        this.high = high;
    }

    public Decimal getLow() {
        return low;
    }

    public void setLow(Decimal low) {
        this.low = low;
    }

    public Decimal getVolume() {
        return volume;
    }

    public void setVolume(Decimal volume) {
        this.volume = volume;
    }
}
