package kr.kuvh.linebot.altcoin.bot.strategy;

import eu.verdelhan.ta4j.*;
import eu.verdelhan.ta4j.indicators.StochasticOscillatorKIndicator;
import eu.verdelhan.ta4j.trading.rules.CrossedDownIndicatorRule;
import eu.verdelhan.ta4j.trading.rules.CrossedUpIndicatorRule;

public class StochasticStrategy {
    public static Strategy buildStrategy(TimeSeries series, int overBuy, int overSell) {
        if (series == null) {
            throw new IllegalArgumentException("Series cannot be null");
        }

        StochasticOscillatorKIndicator stochasticOscillK = new StochasticOscillatorKIndicator(series, 10);

        // Entry rule
        Rule entryRule = new CrossedDownIndicatorRule(stochasticOscillK, Decimal.valueOf(overSell));

        // Exit rule
        Rule exitRule = new CrossedUpIndicatorRule(stochasticOscillK, Decimal.valueOf(overBuy));

        return new BaseStrategy(entryRule, exitRule);
    }
}
