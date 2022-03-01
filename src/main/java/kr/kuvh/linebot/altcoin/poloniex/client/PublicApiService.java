package kr.kuvh.linebot.altcoin.poloniex.client;

import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiChartDataMessage;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiOrderBookMessage;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiVolumeMessage;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.util.List;

/**
 * Created by kuvh on 2017-04-09.
 */
public interface PublicApiService {

    @GET("/public?command=returnChartData")
    Call<List<ApiChartDataMessage>> returnChartData(@Query("currencyPair") String currencyPair, @Query("start") long start, @Query("end") long end, @Query("period") int period);

    @GET("/public?command=return24hVolume")
    Call<ApiVolumeMessage> return24Volume();

    @GET("/public?command=returnOrderBook")
    Call<ApiOrderBookMessage> returnOrderBook(@Query("currencyPair") String currencyPair, @Query("depth") int depth);

}
