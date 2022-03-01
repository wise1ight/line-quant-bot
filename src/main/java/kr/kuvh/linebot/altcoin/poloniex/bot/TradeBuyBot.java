package kr.kuvh.linebot.altcoin.poloniex.bot;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import kr.kuvh.linebot.ApiKeys;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.*;
import kr.kuvh.linebot.util.Time;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.task.TaskExecutor;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kuvh on 2017-04-28.
 */
public class TradeBuyBot implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private LineMessagingClient lineMessagingClient;
    private String currencyPair;
    private TaskExecutor taskExecutor;
    private String mode;
    private BigDecimal vol;

    public TradeBuyBot(LineMessagingClient lineMessagingClient, String currencyPair, TaskExecutor taskExecutor, String mode, BigDecimal vol) {
        this.lineMessagingClient = lineMessagingClient;
        this.currencyPair = currencyPair;
        this.taskExecutor = taskExecutor;
        this.mode = mode;
        this.vol = vol;
    }

    @Override
    public void run() {
        init();
    }

    public void init() {
        TradeBot.isCoinMonitered.put(currencyPair.split("_")[1], true);
        String msg = "[" + mode + "] " + currencyPair + " 종목을 구매할 준비를 하고 있습니다.";
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));

        ApiOrderBookMessage orderBook = TradeApi.getInstance().returnOrderBook(currencyPair, 1);
        BigDecimal bidPrice = orderBook.getBids().get(0).get(0);
        while(true) {
            waitForMillis(10 * 1000);

            orderBook = TradeApi.getInstance().returnOrderBook(currencyPair, 1);
            if(orderBook.getBids().get(0).get(0).compareTo(bidPrice) <= 0) {
                bidPrice = orderBook.getBids().get(0).get(0);
            } else {
                break;
            }
        }

        HashMap<String, ApiCompleteBalanceMessage> balance = TradeApi.getInstance().returnCompleteBalances();
        BigDecimal btcAmount = BigDecimal.ZERO;
        for (Map.Entry<String, ApiCompleteBalanceMessage> entry : balance.entrySet()) {
            if (entry.getValue().getAvailable().signum() != 0) {
                btcAmount = btcAmount.add(entry.getValue().getBtcValue());
            }
        }

        BigDecimal buyBTC = btcAmount.multiply(BigDecimal.valueOf(Const.BUY_RATE * 0.01));
        msg = "[" + mode + "] " + currencyPair + " 종목을 " + buyBTC.toString() + "BTC 어치 구입합니다.";
        logger.info(msg);
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));
        buy(buyBTC);
    }

    private void buy(BigDecimal goalBTCAmount) {
        BigDecimal totalBTCAmount = BigDecimal.ZERO;
        BigDecimal maxBTCRate = BigDecimal.ZERO;

        long timestamp = Time.getCurrentTimestamp();
        int count = 0;
        //TradeApi.returnBalances().get(currencyPair.split("_")[1]).compareTo() > 0
        while(totalBTCAmount.compareTo(goalBTCAmount) <= 0 && count < 5) {
            count++;
            ApiOrderBookMessage orderBook = TradeApi.getInstance().returnOrderBook(currencyPair, 1);
            BigDecimal bidPrice = orderBook.getBids().get(0).get(0);
            if(mode.equals("test") && count > 3)
                bidPrice = orderBook.getAsks().get(0).get(0);

            //BigDecimal bidAmount = orderBook.getBids().get(0).get(1); => Amount
            //BigDecimal remain = goalBTCAmount.subtract(totalBTCAmount);
            //BigDecimal vold = vol.divide(BigDecimal.valueOf(2000), BigDecimal.ROUND_DOWN);
            //BigDecimal limit = remain.compareTo(vold) > 0 ? vold: remain;
            BigDecimal coinAmount = goalBTCAmount.subtract(totalBTCAmount).divide(bidPrice, BigDecimal.ROUND_DOWN);
            if(coinAmount.compareTo(BigDecimal.valueOf(0.0001)) <= 0 || goalBTCAmount.subtract(totalBTCAmount).compareTo(BigDecimal.valueOf(0.0001)) <= 0) {
                break;
            }
            BigDecimal currentBTC = TradeApi.getInstance().returnBalances().get("BTC");
            if(goalBTCAmount.subtract(totalBTCAmount).compareTo(currentBTC) > 0) {
                break;
            }

            bidPrice = bidPrice.add(BigDecimal.valueOf(0.00000001));

            String msg = bidPrice + "BTC 비율로 " + coinAmount.toString() + currencyPair.split("_")[1] + "을 구매 중 입니다.";
            lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));

            TradeApi.getInstance().buy(currencyPair, bidPrice, coinAmount);

            if(maxBTCRate.compareTo(bidPrice) < 0)
                maxBTCRate = bidPrice;

            waitForMillis(10000);

            //취소
            List<ApiOpenOrderMessage> openOrdering = TradeApi.getInstance().returnOpenOrders(currencyPair);
            while(openOrdering != null && openOrdering.size() != 0) {
                for(ApiOpenOrderMessage order : openOrdering) {
                    TradeApi.getInstance().cancelOrder(order.getOrderNumber());
                    //waitForMillis(1000);
                }
                openOrdering = TradeApi.getInstance().returnOpenOrders(currencyPair);
            }

            totalBTCAmount = BigDecimal.ZERO;
            List<ApiTradeHistoryMessage> history = TradeApi.getInstance().returnTradeHistory(currencyPair, timestamp, 0);
            for(ApiTradeHistoryMessage tradeLog : history) {
                totalBTCAmount = totalBTCAmount.add(tradeLog.getTotal());
            }

            //waitForMillis(1000);
        }

        List<ApiOpenOrderMessage> openOrder = TradeApi.getInstance().returnOpenOrders(currencyPair);
        if(openOrder != null) {
            for (ApiOpenOrderMessage order : openOrder) {
                TradeApi.getInstance().cancelOrder(order.getOrderNumber());
                //waitForMillis(1000);
            }
        }

        taskExecutor.execute(new TradeSellBot(lineMessagingClient, currencyPair, maxBTCRate, mode));

        /*
        HashMap<String, Object> tmap = new HashMap<>();
        tmap.put("type", currencyPair.split("_")[1]);
        tmap.put("price", maxBTCRate);
        tmap.put("mode", mode);
        try {
            dbService.insertSession(tmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */

        String msg = currencyPair + " 구매가 끝났습니다.";
        logger.info(msg);
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));
    }

    private void waitForMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
