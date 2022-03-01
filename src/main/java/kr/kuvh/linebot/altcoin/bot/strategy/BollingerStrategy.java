package kr.kuvh.linebot.altcoin.bot.strategy;

import eu.verdelhan.ta4j.*;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.statistics.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.SMAIndicator;
import eu.verdelhan.ta4j.indicators.bollinger.BollingerBandsLowerIndicator;
import eu.verdelhan.ta4j.indicators.bollinger.BollingerBandsMiddleIndicator;
import eu.verdelhan.ta4j.indicators.bollinger.BollingerBandsUpperIndicator;
import eu.verdelhan.ta4j.trading.rules.OverIndicatorRule;
import eu.verdelhan.ta4j.trading.rules.UnderIndicatorRule;

public class BollingerStrategy {
    public static Strategy buildStrategy(TimeSeries series) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }

        ClosePriceIndicator closePrice = new ClosePriceIndicator(series);

        //SMA
        SMAIndicator sma = new SMAIndicator(closePrice, 20);
        StandardDeviationIndicator standardDeviation = new StandardDeviationIndicator(closePrice, 20);
        BollingerBandsMiddleIndicator bbmSMA = new BollingerBandsMiddleIndicator(sma);
        //불린저밴드 상단
        BollingerBandsUpperIndicator bbuSMA = new BollingerBandsUpperIndicator(bbmSMA, standardDeviation);
        //불린저밴드 하단
        BollingerBandsLowerIndicator bblSMA = new BollingerBandsLowerIndicator(bbmSMA, standardDeviation);

        // Entry rule
        Rule entryRule = new UnderIndicatorRule(closePrice, bblSMA);

        // Exit rule
        Rule exitRule = new OverIndicatorRule(closePrice, bbuSMA);

        return new BaseStrategy(entryRule, exitRule);
    }
}
