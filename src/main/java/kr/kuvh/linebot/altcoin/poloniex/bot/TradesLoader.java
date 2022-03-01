package kr.kuvh.linebot.altcoin.poloniex.bot;

import eu.verdelhan.ta4j.*;
import kr.kuvh.linebot.altcoin.api.bittrex.BittrexService;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiChartDataMessage;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexChartData;
import org.knowm.xchange.bittrex.service.BittrexChartDataPeriodType;
import org.knowm.xchange.bittrex.service.BittrexMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.time.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by kuvh on 2017-04-17.
 */
public class TradesLoader {

    public static TimeSeries loadPoloniexSeries(String currencyPair, int startHour, int period) {
        List<ApiChartDataMessage> chartData = TradeApi.getInstance().returnChartData(currencyPair, startHour, period);

        List<Tick> ticks = new ArrayList<Tick>();

        ZonedDateTime beginTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(chartData.get(0).getDate() * 1000), ZoneId.systemDefault());
        ZonedDateTime endTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(chartData.get(chartData.size() - 1).getDate() * 1000), ZoneId.systemDefault());
        if (beginTime.isAfter(endTime)) {
            Collections.reverse(chartData);
        }
        for (ApiChartDataMessage trade : chartData) {
            ZonedDateTime tradeTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(trade.getDate() * 1000), ZoneId.systemDefault());
            Tick tick = new BaseTick(Duration.ofMinutes(period)
                    , tradeTimestamp
                    , Decimal.valueOf(trade.getOpen().toString())
                    , Decimal.valueOf(trade.getHigh().toString())
                    , Decimal.valueOf(trade.getLow().toString())
                    , Decimal.valueOf(trade.getClose().toString())
                    , Decimal.valueOf(trade.getVolume().toString()));
            ticks.add(tick);
        }

        return new BaseTimeSeries("poloniex_" + currencyPair + "_trades", ticks);
    }

    public static TimeSeries loadBittrexSeries(String currencyPair, int period) {
        String[] currencyArr = currencyPair.split("_");
        CurrencyPair pair = new CurrencyPair(currencyArr[0], currencyArr[1]);

        Exchange exchange = BittrexService.getExchange();
        MarketDataService marketDataService = exchange.getMarketDataService();
        List<BittrexChartData> chartData = null;
        try {
            chartData = ((BittrexMarketDataServiceRaw) marketDataService).getBittrexChartData(pair, BittrexChartDataPeriodType.ONE_HOUR);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        List<Tick> ticks = new ArrayList<Tick>();

        ZonedDateTime beginTime = ZonedDateTime.ofInstant(chartData.get(0).getTimeStamp().toInstant(), ZoneId.systemDefault());
        ZonedDateTime endTime = ZonedDateTime.ofInstant(chartData.get(chartData.size() - 1).getTimeStamp().toInstant(), ZoneId.systemDefault());
        if (beginTime.isAfter(endTime)) {
            Collections.reverse(chartData);
        }
        for (BittrexChartData trade : chartData) {
            ZonedDateTime tradeTimestamp = ZonedDateTime.ofInstant(trade.getTimeStamp().toInstant(), ZoneId.systemDefault());
            Tick tick = new BaseTick(Duration.ofMinutes(period)
                    , tradeTimestamp
                    , Decimal.valueOf(trade.getOpen().toString())
                    , Decimal.valueOf(trade.getHigh().toString())
                    , Decimal.valueOf(trade.getLow().toString())
                    , Decimal.valueOf(trade.getClose().toString())
                    , Decimal.valueOf(trade.getVolume().toString()));
            ticks.add(tick);
        }

        return new BaseTimeSeries("bittrex_" + currencyPair + "_trades", ticks);
    }

}
