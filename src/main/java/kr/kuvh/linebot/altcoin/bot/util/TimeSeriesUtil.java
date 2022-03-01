package kr.kuvh.linebot.altcoin.bot.util;

import eu.verdelhan.ta4j.*;
import kr.kuvh.linebot.altcoin.api.bitfinex.BitfinexCandle;
import kr.kuvh.linebot.altcoin.api.bitfinex.BitfinexClient;
import kr.kuvh.linebot.altcoin.api.bittrex.BittrexService;
import kr.kuvh.linebot.altcoin.bot.common.ExchangeEnum;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexChartData;
import org.knowm.xchange.bittrex.service.BittrexChartDataPeriodType;
import org.knowm.xchange.bittrex.service.BittrexMarketDataServiceRaw;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeSeriesUtil {

    public static TimeSeries getTimeSeries(ExchangeEnum exchangeType, String currencyPair, int maxTickCount, int period) throws IOException {
        TimeSeries timeSeries;
        switch (exchangeType) {
            case BITTREX:
                timeSeries = loadBittrexChart(currencyPair, maxTickCount, period);
                break;
            case BITFINEX:
                timeSeries = loadBitfinexChart(currencyPair, maxTickCount, period);
                break;
            default:
                timeSeries = null;
                break;
        }

        return timeSeries;
    }

    public static TimeSeries loadBittrexChart(String currencyPair, int maxTickCount, int period) throws IOException {
        Exchange exchange = BittrexService.getExchange();
        MarketDataService marketDataService = exchange.getMarketDataService();
        BittrexChartDataPeriodType periodType;
        switch (period) {
            case 1:
                periodType = BittrexChartDataPeriodType.ONE_MIN;
                break;
            case 5:
                periodType = BittrexChartDataPeriodType.FIVE_MIN;
                break;
            case 30:
                periodType = BittrexChartDataPeriodType.THIRTY_MIN;
                break;
            case 60:
                periodType = BittrexChartDataPeriodType.ONE_HOUR;
                break;
            case 1440:
                periodType = BittrexChartDataPeriodType.ONE_DAY;
                break;
            default:
                throw new IOException();
        }
        String[] currencyArr = currencyPair.split("_");
        CurrencyPair pair = new CurrencyPair(currencyArr[1], currencyArr[0]);
        List<BittrexChartData> chartData = ((BittrexMarketDataServiceRaw) marketDataService).getBittrexChartData(pair, periodType);

        List<Tick> ticks = new ArrayList<Tick>();

        ZonedDateTime beginTime = ZonedDateTime.ofInstant(chartData.get(0).getTimeStamp().toInstant(), ZoneId.systemDefault());
        ZonedDateTime endTime = ZonedDateTime.ofInstant(chartData.get(chartData.size() - 1).getTimeStamp().toInstant(), ZoneId.systemDefault());
        if (beginTime.isAfter(endTime)) {
            Collections.reverse(chartData);
        }

        int i = chartData.size() - maxTickCount;
        if(i < 0) {
            i = 0;
        }

        for (; i < chartData.size(); i++) {
            BittrexChartData trade = chartData.get(i);
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

    private static TimeSeries loadBitfinexChart(String currencyPair, int maxTickCount, int period) throws IOException {
        String timeFrame;
        switch (period) {
            case 1:
                timeFrame = "1m";
                break;
            case 5:
                timeFrame = "5m";
                break;
            case 15:
                timeFrame = "15m";
                break;
            case 30:
                timeFrame = "30m";
                break;
            case 60:
                timeFrame = "1h";
                break;
            case 180:
                timeFrame = "3h";
                break;
            case 360:
                timeFrame = "6h";
                break;
            case 720:
                timeFrame = "12h";
                break;
            case 1440:
                timeFrame = "1D";
                break;
            case 10080:
                timeFrame = "7D";
                break;
            case 20160:
                timeFrame = "14D";
                break;
            case 43200:
                timeFrame = "1M";
                break;
            default:
                throw new IOException();
        }
        List<BitfinexCandle> chartData = BitfinexClient.getInstance().getService()
                .getCandles(timeFrame, currencyPair.replace("_", ""), System.currentTimeMillis() - maxTickCount * period * 60 * 1000)
                .execute().body();

        List<Tick> ticks = new ArrayList<>();

        ZonedDateTime beginTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(chartData.get(0).getMts()), ZoneId.systemDefault());
        ZonedDateTime endTime = ZonedDateTime.ofInstant(Instant.ofEpochMilli(chartData.get(chartData.size() - 1).getMts()), ZoneId.systemDefault());
        if (beginTime.isAfter(endTime)) {
            Collections.reverse(chartData);
        }
        for (BitfinexCandle trade : chartData) {
            ZonedDateTime tradeTimestamp = ZonedDateTime.ofInstant(Instant.ofEpochMilli(trade.getMts()), ZoneId.systemDefault());
            Tick tick = new BaseTick(Duration.ofMinutes(period)
                    , tradeTimestamp
                    , trade.getOpen()
                    , trade.getHigh()
                    , trade.getLow()
                    , trade.getClose()
                    , trade.getVolume());
            ticks.add(tick);
        }

        return new BaseTimeSeries("bitfinex_" + currencyPair + "_trades", ticks);
    }
}
