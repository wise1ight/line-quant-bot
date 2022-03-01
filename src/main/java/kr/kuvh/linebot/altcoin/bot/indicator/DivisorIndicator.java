package kr.kuvh.linebot.altcoin.bot.indicator;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.indicators.CachedIndicator;

public class DivisorIndicator extends CachedIndicator<Decimal> {

    private Indicator<Decimal> first;

    private Indicator<Decimal> second;

    public DivisorIndicator(Indicator<Decimal> first, Indicator<Decimal> second) {
        super(first);
        this.first = first;
        this.second = second;
    }

    @Override
    protected Decimal calculate(int index) {
        return second.getValue(index).isZero() ? Decimal.ZERO : first.getValue(index).dividedBy(second.getValue(index));
    }
}