package kr.kuvh.linebot.altcoin.bot.indicator;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.indicators.CachedIndicator;

public class PlusIndicator extends CachedIndicator<Decimal> {

    private Indicator<Decimal> first;

    private Indicator<Decimal> second;

    public PlusIndicator(Indicator<Decimal> first, Indicator<Decimal> second) {
        super(first);
        this.first = first;
        this.second = second;
    }

    @Override
    protected Decimal calculate(int index) {
        return first.getValue(index).plus(second.getValue(index));
    }
}