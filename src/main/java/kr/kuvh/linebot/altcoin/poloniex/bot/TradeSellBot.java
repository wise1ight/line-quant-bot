package kr.kuvh.linebot.altcoin.poloniex.bot;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
import kr.kuvh.linebot.ApiKeys;
import kr.kuvh.linebot.altcoin.bot.indicator.TrafficLightIndicator;
import kr.kuvh.linebot.altcoin.bot.strategy.BollingerStrategy;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiCurrencyPairMessage;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiOpenOrderMessage;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiOrderBookMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kuvh on 2017-04-19.
 */
public class TradeSellBot implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private LineMessagingClient lineMessagingClient;
    private String currencyPair;
    private BigDecimal btcPrice;
    private String mode;

    public TradeSellBot(LineMessagingClient lineMessagingClient, String currencyPair, BigDecimal btcPrice, String mode) {
        this.lineMessagingClient = lineMessagingClient;
        this.currencyPair = currencyPair;
        this.btcPrice = btcPrice;
        this.mode = mode;
    }

    @Override
    public void run() {
        init();
    }

    public void init() {
        //waitForMillis(10000);

        TradeBot.isCoinMonitered.put(currencyPair.split("_")[1], true);
        String msg = currencyPair + " 종목 모니터링을 시작합니다.";
        logger.info(msg);
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));

        moniter();
    }

    private void moniter() {
        int sellCount = 0;
        while(true) {
            //TimeSeries series5min = initMovingTimeSeries(currencyPair, 288, 24, 5);
            TimeSeries series30min = initMovingTimeSeries(currencyPair, 192, 24 * 4, 30);
            //TimeSeries series2h = initMovingTimeSeries(currencyPair, 168, 24 * 14, 2 * 60);

            //ClosePriceIndicator closePrice5min = new ClosePriceIndicator(series5min);
            ClosePriceIndicator closePrice30min = new ClosePriceIndicator(series30min);
            //ClosePriceIndicator closePrice2h = new ClosePriceIndicator(series2h);

            //TrafficLightIndicator tli1_5min = new TrafficLightIndicator(closePrice5min, 5, 10, 1);
            //TrafficLightIndicator tli32_5min = new TrafficLightIndicator(closePrice5min, 5, 10, 32);
            TrafficLightIndicator tli1_30min = new TrafficLightIndicator(closePrice30min, 5, 10, 1);
            //TrafficLightIndicator tli32_30min = new TrafficLightIndicator(closePrice30min, 5, 10, 32);
            //TrafficLightIndicator tli1_2h = new TrafficLightIndicator(closePrice2h, 5, 10, 1);
            //TrafficLightIndicator tli32_2h = new TrafficLightIndicator(closePrice2h, 5, 10, 32);

            //CCIIndicator cci5min = new CCIIndicator(series5min, 10);
            //CCIIndicator cci30min = new CCIIndicator(series30min, 10);
            //CCIIndicator cci2h = new CCIIndicator(series2h, 10);

            //OnBalanceVolumeIndicator obv5min = new OnBalanceVolumeIndicator(series5min);
            OnBalanceVolumeIndicator obv30min = new OnBalanceVolumeIndicator(series30min);
            //OnBalanceVolumeIndicator obv2h = new OnBalanceVolumeIndicator(series2h);
            //EMAIndicator ema5min = new EMAIndicator(obv5min, 20);
            //EMAIndicator ema30min = new EMAIndicator(obv30min, 20);
            //EMAIndicator ema2h = new EMAIndicator(obv2h, 20);
            //DifferenceIndicator osc5min = new DifferenceIndicator(obv5min, ema5min);
            //DifferenceIndicator osc30min = new DifferenceIndicator(obv30min, ema30min);
            //DifferenceIndicator osc2h = new DifferenceIndicator(obv2h, ema2h);

            //Strategy bollinger5min = BollingerStrategy.buildStrategy(series5min);
            Strategy bollinger30min = BollingerStrategy.buildStrategy(series30min);
            //Strategy bollinger2h = BollingerStrategy.buildStrategy(series2h);
            //Strategy stochastic5min = StochasticStrategy.buildStrategy(series5min, 80, 20);
            //Strategy stochastic30min = StochasticStrategy.buildStrategy(series30min, 80, 20);
            //Strategy stochastic2h = StochasticStrategy.buildStrategy(series2h, 80, 20);

            //int endIndex5min = series5min.getEndIndex();
            int endIndex30min = series30min.getEndIndex();
            //int endIndex2h = series2h.getEndIndex();

            //boolean isBollingerDown = bollinger30min.shouldEnter(endIndex30min);
            boolean isBollingerUp = bollinger30min.shouldExit(endIndex30min);
            //boolean isOBVGreen2h = osc30min.getValue(endIndex30min).isPositive();
            boolean isTLS1Green = tli1_30min.getValue(endIndex30min);
            //boolean isTLS32Green = tli32_30min.getValue(endIndex30min);
            //boolean isOverSell = stochastic30min.shouldEnter(endIndex30min);
            //boolean isOverBuy = stochastic30min.shouldExit(endIndex30min);

            //취소
            List<ApiOpenOrderMessage> openOrdering = TradeApi.getInstance().returnOpenOrders(currencyPair);
            while(openOrdering != null && openOrdering.size() != 0) {
                for(ApiOpenOrderMessage order : openOrdering) {
                    TradeApi.getInstance().cancelOrder(order.getOrderNumber());
                    //waitForMillis(1000);
                }
                openOrdering = TradeApi.getInstance().returnOpenOrders(currencyPair);
            }

            BigDecimal currentATC = TradeApi.getInstance().returnBalances().get(currencyPair.split("_")[1]);
            //waitForMillis(2 * 1000);
            ApiOrderBookMessage orderBook = TradeApi.getInstance().returnOrderBook(currencyPair, 1);
            BigDecimal askPrice = orderBook.getAsks().get(0).get(0);
            if (sellCount > 1) {
                askPrice = orderBook.getBids().get(0).get(0);
            }

            if (currentATC.multiply(askPrice).compareTo(BigDecimal.valueOf(0.0001)) <= 0) {
                break;
            }
            //이득1
            if (askPrice.compareTo(btcPrice.multiply(BigDecimal.valueOf(1.02))) >= 0 && obv30min.getValue(endIndex30min - 1).isGreaterThan(obv30min.getValue(endIndex30min)) && isBollingerUp) {
                while(true) {
                    waitForMillis(10 * 1000);

                    orderBook = TradeApi.getInstance().returnOrderBook(currencyPair, 1);
                    if(orderBook.getAsks().get(0).get(0).compareTo(askPrice) >= 0) {
                        askPrice = orderBook.getAsks().get(0).get(0);
                    } else {
                        break;
                    }
                }

                if(askPrice.compareTo(BigDecimal.valueOf(0.00001)) > 0)
                    askPrice = askPrice.subtract(BigDecimal.valueOf(0.00000001));

                sell(askPrice);
            } else if (askPrice.compareTo(btcPrice.multiply(BigDecimal.valueOf(1.1))) >= 0 || askPrice.compareTo(btcPrice.multiply(BigDecimal.valueOf(0.9))) < 0) {
                askPrice = askPrice.subtract(BigDecimal.valueOf(0.00000001));
                sell(askPrice);
            }

            /* else if (mode.equals("switch1") && !isCCIGreen2h && !isOBVGreen2h) {
                askPrice = askPrice.subtract(BigDecimal.valueOf(0.00000001));
                sell(askPrice);
            } else if ((mode.equals("bullish1") || mode.equals("bearish2")) && !isTLS32Green) {
                askPrice = askPrice.subtract(BigDecimal.valueOf(0.00000001));
                sell(askPrice);
            }
            */

            sellCount++;
            waitForMillis(20 * 1000);
        }

        String msg = currencyPair.split("_")[1] + "거래를 종료합니다.";
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));
        HashMap<String, Object> query = new HashMap<>();
        query.put("type", currencyPair.split("_")[1]);
        /*
        try {
            List<Map<String, Object>> result = dbService.getSessionIdx(query);
            int idx = (int)result.get(0).get("idx");
            query.clear();
            query.put("idx", idx);
            dbService.completeSession(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        TradeBot.isCoinMonitered.put(currencyPair.split("_")[1], false);

    }

    private void sell(BigDecimal askPrice) {
        BigDecimal currentATC = TradeApi.getInstance().returnBalances().get(currencyPair.split("_")[1]);

        BigDecimal percent = askPrice.subtract(btcPrice).divide(btcPrice, BigDecimal.ROUND_DOWN).multiply(BigDecimal.valueOf(100)).setScale(3, RoundingMode.HALF_UP);
        String msg = askPrice.toString() + "BTC 비율로 " + currentATC.toString() + currencyPair.split("_")[1] + "을 " + percent.toString() + "% 판매 중 입니다.";
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));

        //waitForMillis(2 * 1000);

        //판매
        ApiCurrencyPairMessage sellMsg = TradeApi.getInstance().sell(currencyPair, askPrice, currentATC);
    }

    private void waitForMillis(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private TimeSeries initMovingTimeSeries(String currencyPair, int maxTickCount, int startHour, int period) {
        TimeSeries series = TradesLoader.loadPoloniexSeries(currencyPair, startHour, period);
        series.setMaximumTickCount(maxTickCount);
        return series;
    }
}