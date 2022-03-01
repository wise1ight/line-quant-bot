package kr.kuvh.linebot.altcoin.bot.indicator;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.CachedIndicator;
import eu.verdelhan.ta4j.indicators.helpers.AbsoluteIndicator;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.DifferenceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.MultiplierIndicator;
import eu.verdelhan.ta4j.indicators.EMAIndicator;

public class WTMIndicator extends CachedIndicator<Decimal> {

    private final int n1;
    private final EMAIndicator esa;
    private final EMAIndicator d;
    private final Indicator<Decimal> ci;
    private final EMAIndicator wtm;

    public WTMIndicator(TimeSeries timeSeries, int l, int len) {
        super(timeSeries);
        ClosePriceIndicator src = new ClosePriceIndicator(timeSeries);
        n1 = len * l;
        esa = new EMAIndicator(src, n1);
        d = new EMAIndicator(new AbsoluteIndicator(new DifferenceIndicator(src, esa)), n1);
        ci = new DivisorIndicator(new DifferenceIndicator(src, esa), new MultiplierIndicator(d, Decimal.valueOf(0.015)));
        wtm = new EMAIndicator(ci, n1 * 2 + 1);
    }

    @Override
    protected Decimal calculate(int index) {
        return wtm.getValue(index);
    }
}
