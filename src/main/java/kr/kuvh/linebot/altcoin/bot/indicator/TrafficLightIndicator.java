package kr.kuvh.linebot.altcoin.bot.indicator;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.indicators.CachedIndicator;
import eu.verdelhan.ta4j.indicators.helpers.AbsoluteIndicator;
import eu.verdelhan.ta4j.indicators.helpers.DifferenceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.MultiplierIndicator;
import eu.verdelhan.ta4j.indicators.EMAIndicator;

public class TrafficLightIndicator extends CachedIndicator<Boolean> {
    private final Indicator<Decimal> src;
    private final int n1;
    private final int smoth;
    private final EMAIndicator esa;
    private final EMAIndicator d;
    private final Indicator<Decimal> ci;
    private final EMAIndicator wtm;

    public TrafficLightIndicator(Indicator<Decimal> src, int len, int smoth, int l) {
        super(src);
        this.src = src;
        this.smoth = smoth;
        this.n1 = len * l;
        this.esa = new EMAIndicator(src, n1);
        this.d = new EMAIndicator(new AbsoluteIndicator(new DifferenceIndicator(src, esa)), n1);
        this.ci = new DivisorIndicator(new DifferenceIndicator(src, esa), new MultiplierIndicator(d, Decimal.valueOf(0.015)));
        this.wtm = new EMAIndicator(ci, n1 * 2 + 1);
    }

    @Override
    protected Boolean calculate(int index) {
        Decimal current = wtm.getValue(index);
        Decimal previous = (index - smoth) >= 0 ? wtm.getValue(index - smoth) : Decimal.ZERO;
        return current.isGreaterThanOrEqual(previous);
    }
}
