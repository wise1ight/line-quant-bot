package kr.kuvh.linebot.altcoin.poloniex.client;

import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.*;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuvh on 2017-04-12.
 */
public interface TradingApiService {

    @POST("/tradingApi")
    @FormUrlEncoded
    Call<HashMap<String, BigDecimal>> returnBalance(@FieldMap Map<String, String> fields);

    @POST("/tradingApi")
    @FormUrlEncoded
    Call<HashMap<String, ApiCompleteBalanceMessage>> returnCompleteBalances(@FieldMap Map<String, String> fields);

    @POST("/tradingApi")
    @FormUrlEncoded
    Call<List<ApiOpenOrderMessage>> returnOpenOrders(@FieldMap Map<String, String> fields);

    @POST("/tradingApi")
    @FormUrlEncoded
    Call<ApiCurrencyPairMessage> requestOrder(@FieldMap Map<String, String> fields);

    @POST("/tradingApi")
    @FormUrlEncoded
    Call<ApiCancelOrderMessage> cancelOrder(@FieldMap Map<String, String> fields);

    @POST("/tradingApi")
    @FormUrlEncoded
    Call<List<ApiTradeHistoryMessage>> returnTradeHistory(@FieldMap Map<String, String> fields);

}
