package kr.kuvh.linebot.altcoin.bot.watchdog;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.AverageTrueRangeIndicator;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import kr.kuvh.linebot.altcoin.bot.TradingEngine;
import kr.kuvh.linebot.altcoin.bot.common.BitcoinMarketEnum;
import kr.kuvh.linebot.altcoin.bot.common.ExchangeEnum;
import kr.kuvh.linebot.util.MessageLoggerUtil;
import kr.kuvh.linebot.altcoin.bot.util.TimeSeriesUtil;
import kr.kuvh.linebot.altcoin.bot.indicator.TrafficLightIndicator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

//큰형님 가격 감시
@Component
@ComponentScan(basePackages = {"kr.kuvh.linebot"})
public class BitcoinWatchdog extends Watchdog {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TradingEngine tradingEngine;

    public BitcoinWatchdog(TradingEngine tradingEngine) {
        this.tradingEngine = tradingEngine;
        setCycleMillis(30 * 1000);
    }

    @Override
    protected void runCycle() {
        try {
            TimeSeries timeSeries5min = TimeSeriesUtil.getTimeSeries(ExchangeEnum.BITFINEX, "BTC_USD", 24, 5);
            TimeSeries timeSeries1h = TimeSeriesUtil.getTimeSeries(ExchangeEnum.BITFINEX, "BTC_USD", 24, 60);
            ClosePriceIndicator closePrice1h = new ClosePriceIndicator(timeSeries1h);
            AverageTrueRangeIndicator atr = new AverageTrueRangeIndicator(timeSeries5min, 14);

            int endIndex5min = timeSeries5min.getEndIndex();
            int endIndex1h = timeSeries1h.getEndIndex();

            //장 상황
            if(atr.getValue(endIndex5min).compareTo(closePrice1h.getValue(endIndex1h - 3).multipliedBy(Decimal.valueOf(0.005))) < 0) {
                tradingEngine.setBitcoinMarketStatus(BitcoinMarketEnum.CROSSBAR);
            } else {
                tradingEngine.setBitcoinMarketStatus(BitcoinMarketEnum.NOT_CROSSBAR);
            }

            //장 추세
            TrafficLightIndicator tli1_1h = new TrafficLightIndicator(closePrice1h, 10, 50, 1);
            if(tli1_1h.getValue(endIndex1h)) {
                tradingEngine.setBitcoinMarketTrend(BitcoinMarketEnum.UPPER);
            } else {
                tradingEngine.setBitcoinMarketTrend(BitcoinMarketEnum.LOWER);
            }
        } catch (Exception e) {
            tradingEngine.setBitcoinMarketStatus(BitcoinMarketEnum.INIT);
            tradingEngine.setBitcoinMarketTrend(BitcoinMarketEnum.INIT);
            MessageLoggerUtil.pushErrorMessage(logger, e.getMessage());
            e.printStackTrace();
        }
    }
}
