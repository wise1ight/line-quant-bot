package kr.kuvh.linebot.altcoin.poloniex.bot;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import eu.verdelhan.ta4j.*;
import eu.verdelhan.ta4j.indicators.CCIIndicator;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.DifferenceIndicator;
import eu.verdelhan.ta4j.indicators.EMAIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
import kr.kuvh.linebot.ApiKeys;
import kr.kuvh.linebot.altcoin.bot.indicator.TrafficLightIndicator;
import kr.kuvh.linebot.altcoin.bot.strategy.BollingerStrategy;
import kr.kuvh.linebot.altcoin.bot.strategy.StochasticStrategy;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiVolumeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class TradeBot {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LineMessagingClient lineMessagingClient;

    public static HashMap<String, Boolean> isCoinMonitered;

    public TradeBot() {
        isCoinMonitered = new HashMap<>();
    }

    public void init() {
        String msg = "트레이딩 봇 작동을 시작합니다.";
        logger.info(msg);
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));

        //갑작스러운 종료로 인해 죽은 모니터링 세션 부활
        //restoreMoniterBot();
    }

    /*
    public void restoreMoniterBot() {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list.addAll(dbService.getUncompletedSession());
        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));
        }

        for(Map<String, Object> map : list) {
            taskExecutor.execute(new TradeSellBot(lineMessagingClient, dbService, "BTC_" + map.get("type"), (BigDecimal)map.get("price"), (String)map.get("mode")));
            waitForMillis(5000);
        }
    }
    */

    public synchronized void analysisChart() {
        logger.info("차트 분석중...");

        ApiVolumeMessage volMsg = TradeApi.getInstance().return24Volume();
        Map<String, Map<String, BigDecimal>> vols = volMsg.getProperties();
        HashMap<String, BigDecimal> btcVols = new HashMap<>();
        for(Map.Entry<String, Map<String, BigDecimal>> entry : vols.entrySet()) {
            String[] pair = entry.getKey().split("_");
            if(pair[0].equals("BTC") && entry.getValue().get("BTC").compareTo(BigDecimal.valueOf(100)) >= 0) {
                btcVols.put(pair[1], entry.getValue().get("BTC"));
            }
        }

        List<String> keysetList = new ArrayList<>();
        keysetList.addAll(btcVols.keySet());
        Collections.sort(keysetList, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return btcVols.get(o2).compareTo(btcVols.get(o1));
            }
        });

        for(String coinName : keysetList) {
            if(coinName.equals("DOGE") || coinName.equals("XRP") || coinName.equals("STR") || coinName.equals("BCN")) {
                continue;
            }
            boolean isExist = false;
            /*
            try {
                List<Map<String, Object>> list = dbService.getUncompletedSession();
                for(Map<String, Object> map : list) {
                    if(map.get("type").equals(coinName)) {
                        isExist = true;
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            */

            boolean isMonitered = false;
            if(isCoinMonitered.get(coinName) != null) {
                isMonitered = isCoinMonitered.get(coinName);
            }
            if(!isExist && !isMonitered) {
                analysisAndBuy("BTC_" + coinName, btcVols.get(coinName));
            }
        }
    }

    public void analysisAndBuy(String currencyPair, BigDecimal vol) {
        logger.info(currencyPair);

        TimeSeries series5min = initMovingTimeSeries(currencyPair, 288, 24, 5);
        TimeSeries series30min = initMovingTimeSeries(currencyPair, 192, 24 * 4, 30);
        TimeSeries series2h = initMovingTimeSeries(currencyPair, 168, 24 * 14, 2 * 60);

        ClosePriceIndicator closePrice5min = new ClosePriceIndicator(series5min);
        ClosePriceIndicator closePrice30min = new ClosePriceIndicator(series30min);
        ClosePriceIndicator closePrice2h = new ClosePriceIndicator(series2h);

        TrafficLightIndicator tli1_5min = new TrafficLightIndicator(closePrice5min, 5, 10, 1);
        //TrafficLightIndicator tli32_5min = new TrafficLightIndicator(closePrice5min, 5, 10, 32);
        TrafficLightIndicator tli1_30min = new TrafficLightIndicator(closePrice30min, 5, 10, 1);
        //TrafficLightIndicator tli2_30min = new TrafficLightIndicator(closePrice30min, 5, 10, 2);
        //TrafficLightIndicator tli4_30min = new TrafficLightIndicator(closePrice30min, 5, 10, 4);
        //TrafficLightIndicator tli8_30min = new TrafficLightIndicator(closePrice30min, 5, 10, 8);
        //TrafficLightIndicator tli16_30min = new TrafficLightIndicator(closePrice30min, 5, 10, 16);
        //TrafficLightIndicator tli32_30min = new TrafficLightIndicator(closePrice30min, 5, 10, 32);
        TrafficLightIndicator tli1_2h = new TrafficLightIndicator(closePrice2h, 5, 10, 1);
        //TrafficLightIndicator tli32_2h = new TrafficLightIndicator(closePrice2h, 5, 10, 32);

        //CCIIndicator cci5min = new CCIIndicator(series5min, 10);
        //CCIIndicator cci30min = new CCIIndicator(series30min, 10);
        CCIIndicator cci2h = new CCIIndicator(series2h, 10);

        OnBalanceVolumeIndicator obv5min = new OnBalanceVolumeIndicator(series5min);
        OnBalanceVolumeIndicator obv30min = new OnBalanceVolumeIndicator(series30min);
        OnBalanceVolumeIndicator obv2h = new OnBalanceVolumeIndicator(series2h);
        //EMAIndicator ema5min = new EMAIndicator(obv5min, 20);
        //EMAIndicator ema30min = new EMAIndicator(obv30min, 20);
        EMAIndicator ema2h = new EMAIndicator(obv2h, 20);
        //DifferenceIndicator osc5min = new DifferenceIndicator(obv5min, ema5min);
        //DifferenceIndicator osc30min = new DifferenceIndicator(obv30min, ema30min);
        DifferenceIndicator osc2h = new DifferenceIndicator(obv2h, ema2h);

        //Strategy bollinger5min = BollingerStrategy.buildStrategy(series5min);
        //Strategy bollinger30min = BollingerStrategy.buildStrategy(series30min);
        Strategy bollinger2h = BollingerStrategy.buildStrategy(series2h);

        Strategy stochastic5min = StochasticStrategy.buildStrategy(series5min, 80, 20);
        Strategy stochastic30min = StochasticStrategy.buildStrategy(series30min, 80, 20);
        Strategy stochastic2h = StochasticStrategy.buildStrategy(series2h, 80, 20);

        int endIndex5min = series5min.getEndIndex();
        int endIndex30min = series30min.getEndIndex();
        int endIndex2h = series2h.getEndIndex();

        boolean isPreBollingerDown = bollinger2h.shouldEnter(endIndex2h - 1);
        //boolean isBollingerDown = bollinger30min.shouldEnter(endIndex30min);
        //boolean isBollingerUp = bollinger30min.shouldExit(endIndex30min);
        //boolean isOBVGreen = osc30min.getValue(endIndex30min).isPositive();
        //boolean isOBVGreen2h = osc2h.getValue(endIndex2h).isPositive();
        boolean isPreOBVGreen = osc2h.getValue(endIndex2h - 1).isPositive();
        //boolean isCCIGreen = cci30min.getValue(endIndex30min).isPositive();
        boolean isPreCCIGreen = cci2h.getValue(endIndex2h - 1).isPositive();
        //boolean isTLS1Green = tli1_30min.getValue(endIndex30min);
        //boolean isTLS2Green = tli2_30min.getValue(endIndex30min);
        //boolean isTLS4Green = tli4_30min.getValue(endIndex30min);
        //boolean isTLS8Green = tli8_30min.getValue(endIndex30min);
        //boolean isTLS16Green = tli16_30min.getValue(endIndex30min);
        //boolean isTLS32Green = tli32_30min.getValue(endIndex30min);
        boolean isPreTLS1Green = tli1_2h.getValue(endIndex2h - 1);
        boolean isPreTLS1Green30min = tli1_30min.getValue(endIndex30min - 1);
        boolean isPreTLS1Green5min = tli1_5min.getValue(endIndex5min - 1);
        //boolean isPreTLS32Green = tli32_30min.getValue(endIndex30min - 1);
        //boolean isOverSell = stochastic30min.shouldEnter(endIndex30min);
        boolean isPreOverSell = stochastic2h.shouldEnter(endIndex2h - 1);
        boolean isPreOverSell30min = stochastic30min.shouldEnter(endIndex30min - 1);
        boolean isPreOverSell5min = stochastic5min.shouldEnter(endIndex5min - 1);
        //boolean isOverBuy = stochastic30min.shouldExit(endIndex30min);

        /*
        if (isBollingerDown && !isTLS1Green5min && isTLS32Green && isOverSell && isTLS1Green2h) {
            //하락장
            taskExecutor.execute(new TradeBuyBot(lineMessagingClient, dbService, currencyPair, taskExecutor, "bearish2", vol));
        } else if ((!isPreCCIGreen2h || !isPreOBVGreen2h) && isCCIGreen2h && isOBVGreen2h && isTLS1Green2h) {
            //추세 전환
            taskExecutor.execute(new TradeBuyBot(lineMessagingClient, dbService, currencyPair, taskExecutor, "switch1", vol));
        } else if (!isPreTLS32Green && isTLS32Green && isTLS16Green && isTLS8Green && isTLS4Green && isTLS2Green && isTLS1Green && isTLS1Green2h) {
            //상승장
           taskExecutor.execute(new TradeBuyBot(lineMessagingClient, dbService, currencyPair, taskExecutor, "bullish1", vol));
        }
        */
        // && isPreBollingerDown

        //isPreOverSell &&
        if (!isPreTLS1Green && obv2h.getValue(endIndex2h - 1).isLessThan(obv2h.getValue(endIndex2h)) && !isPreCCIGreen && !isPreOBVGreen && isPreBollingerDown) {
            if(!isPreTLS1Green30min && isPreOverSell30min && obv30min.getValue(endIndex30min - 1).isLessThan(obv30min.getValue(endIndex30min))) {
                //if(!isPreTLS1Green5min && isPreOverSell5min && obv5min.getValue(endIndex5min - 1).isLessThan(obv5min.getValue(endIndex5min))) {
                    //taskExecutor.execute(new TradeBuyBot(lineMessagingClient, dbService, currencyPair, taskExecutor, "test", vol));
                //}
            }
        }

        waitForMillis(5000);
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
