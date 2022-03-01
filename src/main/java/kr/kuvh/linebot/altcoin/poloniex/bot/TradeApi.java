package kr.kuvh.linebot.altcoin.poloniex.bot;

import kr.kuvh.linebot.altcoin.poloniex.client.ApiClient;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.*;
import kr.kuvh.linebot.util.Time;
import retrofit2.Response;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kuvh on 2017-04-17.
 */
public class TradeApi {

    private static TradeApi instance;

    public static synchronized TradeApi getInstance() {
        if (instance == null) {
            instance = new TradeApi();
        }
        return instance;
    }

    public HashMap<String, BigDecimal> returnBalances() {
        HashMap<String, String> map = new HashMap<>();
        map.put("command", "returnBalances");

        HashMap<String, BigDecimal> balances = null;
        while(balances == null) {
            try {
                //map.put("nonce", getNonce());
                balances = ApiClient.getInstance().getTradingApiService()
                        .returnBalance(map)
                        .execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            waitForMillis(3000);
        }
        return balances;
    }

    public HashMap<String, ApiCompleteBalanceMessage> returnCompleteBalances() {
        HashMap<String, String> map = new HashMap<>();
        map.put("command", "returnCompleteBalances");

        HashMap<String, ApiCompleteBalanceMessage> balances = null;
        while(balances == null) {
            try {
                //map.put("nonce", getNonce());
                balances = ApiClient.getInstance().getTradingApiService()
                        .returnCompleteBalances(map)
                        .execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            waitForMillis(3000);
        }

        return balances;
    }

    public List<ApiChartDataMessage> returnChartData(String currencyPair, int startHour, int period) {
        List<ApiChartDataMessage> chartData = null;
        while(chartData == null) {
            try {
                chartData = ApiClient.getInstance().getPublicApiService()
                        .returnChartData(currencyPair, Time.getCurrentTimestamp() - startHour * 60 * 60, 9999999999L, 60 * period)
                        .execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return chartData;
    }

    public ApiVolumeMessage return24Volume() {
        ApiVolumeMessage message = null;
        while(message == null) {
            try {
                message = ApiClient.getInstance().getPublicApiService()
                        .return24Volume()
                        .execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return message;
    }

    public List<ApiOpenOrderMessage> returnOpenOrders(String currencyPair) {
        HashMap<String, String> map = new HashMap<>();
        map.put("command", "returnOpenOrders");
        map.put("currencyPair", currencyPair);

        List<ApiOpenOrderMessage> message = null;
        while(message == null) {
            try {
                //map.put("nonce", getNonce());
                message = ApiClient.getInstance().getTradingApiService()
                        .returnOpenOrders(map)
                        .execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }

            waitForMillis(3000);
        }

        return message;
    }

    public ApiOrderBookMessage returnOrderBook(String currencyPair, int depth) {
        ApiOrderBookMessage message = null;
        while(message == null) {
            try {
                message = ApiClient.getInstance().getPublicApiService()
                        .returnOrderBook(currencyPair, depth)
                        .execute().body();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return message;
    }

    public ApiCurrencyPairMessage buy(String currencyPair, BigDecimal rate, BigDecimal amount) {
        HashMap<String, String> map = new HashMap<>();
        map.put("command", "buy");
        map.put("currencyPair", currencyPair);
        map.put("rate", rate.setScale(8, BigDecimal.ROUND_DOWN).toString());
        map.put("amount", amount.setScale(8, BigDecimal.ROUND_DOWN).toString());

        ApiCurrencyPairMessage message = null;

        try {
            //map.put("nonce", getNonce());
            message = ApiClient.getInstance().getTradingApiService()
                    .requestOrder(map)
                    .execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    public ApiCurrencyPairMessage sell(String currencyPair, BigDecimal rate, BigDecimal amount) {
        HashMap<String, String> map = new HashMap<>();
        map.put("command", "sell");
        map.put("currencyPair", currencyPair);
        map.put("rate", rate.setScale(8, BigDecimal.ROUND_DOWN).toString());
        map.put("amount", amount.setScale(8, BigDecimal.ROUND_DOWN).toString());

        ApiCurrencyPairMessage message = null;

        try {
            //map.put("nonce", getNonce());
            message = ApiClient.getInstance().getTradingApiService()
                    .requestOrder(map)
                    .execute().body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;

    }

    public ApiCancelOrderMessage cancelOrder(long orderNumber) {
        HashMap<String, String> map = new HashMap<>();
        map.put("command", "cancelOrder");
        map.put("orderNumber", String.valueOf(orderNumber));

        ApiCancelOrderMessage message = null;
        try {
            //map.put("nonce", getNonce());
            Response<ApiCancelOrderMessage> response = ApiClient.getInstance().getTradingApiService()
                    .cancelOrder(map)
                    .execute();
                    message = response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return message;
    }

    public List<ApiTradeHistoryMessage> returnTradeHistory(String currencyPair, long start, long end) {
        HashMap<String, String> map = new HashMap<>();
        map.put("command", "returnTradeHistory");
        map.put("currencyPair", currencyPair);
        if(start != 0)
            map.put("start", String.valueOf(start));
        if(end != 0)
            map.put("end", String.valueOf(end));

        List<ApiTradeHistoryMessage> message = null;
        while(message == null) {
            try {
                //map.put("nonce", getNonce());
                message = ApiClient.getInstance().getTradingApiService()
                        .returnTradeHistory(map)
                        .execute().body();
            } catch (Exception e) {
                e.printStackTrace();
            }
            waitForMillis(3000);
        }

        return message;
    }

    private void waitForMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
