package kr.kuvh.linebot.altcoin.bot.watchdog;

import eu.verdelhan.ta4j.Strategy;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.CCIIndicator;
import eu.verdelhan.ta4j.indicators.EMAIndicator;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.DifferenceIndicator;
import eu.verdelhan.ta4j.indicators.volume.OnBalanceVolumeIndicator;
import kr.kuvh.linebot.altcoin.api.bittrex.BittrexService;
import kr.kuvh.linebot.altcoin.bot.TradingEngine;
import kr.kuvh.linebot.altcoin.bot.common.BitcoinMarketEnum;
import kr.kuvh.linebot.altcoin.bot.common.ExchangeEnum;
import kr.kuvh.linebot.altcoin.bot.util.TimeSeriesUtil;
import kr.kuvh.linebot.altcoin.bot.indicator.*;
import kr.kuvh.linebot.altcoin.bot.strategy.*;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexMarketSummary;
import org.knowm.xchange.bittrex.dto.marketdata.BittrexSymbol;
import org.knowm.xchange.bittrex.service.BittrexMarketDataServiceRaw;
import org.knowm.xchange.currency.Currency;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class BittrexChartWatchdog extends Watchdog {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private TradingEngine tradingEngine;
    private Exchange exchange;
    private BittrexMarketDataServiceRaw marketDataService;

    private static final String CURRENCY_BTC = "BTC";
    private static final int MIN_VOLUME = 100;

    public BittrexChartWatchdog(TradingEngine tradingEngine) {
        this.tradingEngine = tradingEngine;
        this.exchange = BittrexService.getExchange();
        this.marketDataService = (BittrexMarketDataServiceRaw) exchange.getMarketDataService();
        setCycleMillis(30 * 1000);
    }

    @Override
    protected void runCycle() {
        //장세를 분석한다
        if(!tradingEngine.getBitcoinMarketStatus().equals(BitcoinMarketEnum.CROSSBAR)) {
            return;
        }

        boolean isSuccess = false;
        List<BittrexMarketSummary> marketSummaries = null;
        while(!isSuccess) {
            try {
                marketSummaries = marketDataService.getBittrexMarketSummaries();
                isSuccess = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        isSuccess = false;
        List<BittrexSymbol> symbols = null;
        while(!isSuccess) {
            try {
                symbols = this.marketDataService.getBittrexSymbols();
                isSuccess = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(BittrexMarketSummary marketSummary : marketSummaries) {
            //isBTCBaseMarket??
            if(!marketSummary.getMarketName().contains("BTC-")) {
                continue;
            }

            boolean isMarketFine = false;
            String currency = marketSummary.getMarketName().replace("BTC-", "");
            for(BittrexSymbol symbol : symbols) {
                if(symbol.getMarketName().equals(CURRENCY_BTC + "-" + currency) && symbol.getIsActive()) {
                    isMarketFine = true;
                    break;
                }
            }

            if(isMarketFine && marketSummary.getBaseVolume().compareTo(BigDecimal.valueOf(MIN_VOLUME)) > 0) {
                analyzeCharts(currency);
            }
        }
    }

    private void analyzeCharts(String currency) {
        String currencyPair = CURRENCY_BTC + "_" + currency;

        /*
        TimeSeries timeSeries5min = null;
        boolean isSuccess = false;
        while(!isSuccess) {
            try {
                timeSeries5min = TimeSeriesUtil.getTimeSeries(ExchangeEnum.BITTREX, currencyPair, 192, 5);
            } catch (IOException e) {
                e.printStackTrace();
            }
            isSuccess = true;
        }
        */

        TimeSeries timeSeries30min = null;
        boolean isSuccess = false;
        while(!isSuccess) {
            try {
                timeSeries30min = TimeSeriesUtil.getTimeSeries(ExchangeEnum.BITTREX, currencyPair, 192, 30);
                isSuccess = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TimeSeries timeSeries1h = null;
        isSuccess = false;
        while(!isSuccess) {
            try {
                timeSeries1h = TimeSeriesUtil.getTimeSeries(ExchangeEnum.BITTREX, currencyPair, 192, 60);
                isSuccess = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ClosePriceIndicator closePrice30min = new ClosePriceIndicator(timeSeries30min);
        ClosePriceIndicator closePrice1h = new ClosePriceIndicator(timeSeries1h);

        TrafficLightIndicator tli1_30min = new TrafficLightIndicator(closePrice30min, 5, 10, 1);
        TrafficLightIndicator tli1_1h = new TrafficLightIndicator(closePrice1h, 5, 10, 1);

        CCIIndicator cci1h = new CCIIndicator(timeSeries1h, 10);

        OnBalanceVolumeIndicator obv30min = new OnBalanceVolumeIndicator(timeSeries30min);
        OnBalanceVolumeIndicator obv1h = new OnBalanceVolumeIndicator(timeSeries1h);
        EMAIndicator ema1h = new EMAIndicator(obv1h, 20);
        DifferenceIndicator osc1h = new DifferenceIndicator(obv1h, ema1h);

        Strategy bollinger1h = BollingerStrategy.buildStrategy(timeSeries1h);

        Strategy stochastic30min = StochasticStrategy.buildStrategy(timeSeries30min, 80, 20);

        int endIndex30min = timeSeries30min.getEndIndex();
        int endIndex1h = timeSeries1h.getEndIndex();

        boolean isPreBollingerDown = bollinger1h.shouldEnter(endIndex1h - 1);
        boolean isPreOBVGreen = osc1h.getValue(endIndex1h - 1).isPositive();
        boolean isPreCCIGreen = cci1h.getValue(endIndex1h - 1).isPositive();
        boolean isPreTLS1Green = tli1_1h.getValue(endIndex1h - 1);
        boolean isPreTLS1Green30min = tli1_30min.getValue(endIndex30min - 1);
        boolean isPreOverSell30min = stochastic30min.shouldEnter(endIndex30min - 1);

        if (!isPreTLS1Green && obv1h.getValue(endIndex1h - 1).isLessThan(obv1h.getValue(endIndex1h)) && !isPreCCIGreen && !isPreOBVGreen && isPreBollingerDown) {
            if(!isPreTLS1Green30min && isPreOverSell30min && obv30min.getValue(endIndex30min - 1).isLessThan(obv30min.getValue(endIndex30min))) {
                tradingEngine.createNormalTrader(ExchangeEnum.BITTREX, new CurrencyPair(Currency.BTC, new Currency(currency)));
            }
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
