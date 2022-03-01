package kr.kuvh.linebot.altcoin.api.bitfinex;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface BitfinexV2Service {
    @GET("v2/candles/trade:{timeFrame}:t{symbol}/hist")
    Call<List<BitfinexCandle>> getCandles(@Path("timeFrame") String timeFrame, @Path("symbol") String symbol, @Query("start") long start);
}
