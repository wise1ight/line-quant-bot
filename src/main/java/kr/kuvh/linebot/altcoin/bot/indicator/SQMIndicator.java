package kr.kuvh.linebot.altcoin.bot.indicator;

import eu.verdelhan.ta4j.Decimal;
import eu.verdelhan.ta4j.Indicator;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.CachedIndicator;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.DifferenceIndicator;
import eu.verdelhan.ta4j.indicators.helpers.MultiplierIndicator;
import eu.verdelhan.ta4j.indicators.statistics.StandardDeviationIndicator;
import eu.verdelhan.ta4j.indicators.SMAIndicator;

public class SQMIndicator extends CachedIndicator<Decimal> {

    private final int len1;
    private final boolean useTrueRange;
    private final Decimal multKC;
    private final ClosePriceIndicator src;
    private final SMAIndicator basis;
    private final Indicator<Decimal> dev;
    private final Indicator<Decimal> upperBB;
    private final Indicator<Decimal> lowerBB;
    private final SMAIndicator ma;
    //private final Indicator<Decimal> val;

    public SQMIndicator(TimeSeries timeSeries, int l, int len, Decimal multKC, boolean useTrueRange) {
        super(timeSeries);
        len1 = len * l;
        this.useTrueRange = useTrueRange;
        this.multKC = multKC;
        src = new ClosePriceIndicator(timeSeries);
        basis = new SMAIndicator(src, len1);
        dev = new MultiplierIndicator(new StandardDeviationIndicator(src, len1), multKC);
        upperBB = new PlusIndicator(basis, dev);
        lowerBB = new DifferenceIndicator(basis, dev);
        ma = new SMAIndicator(src, len1);



        /*
        range = useTrueRange ? max(high - low, high - close[1], low - close[1]) : (high - low);
        rangema = new SMAIndicator(range, len1);
        upperKC = new PlusIndicator(ma, new MultiplierIndicator(rangema, multKC));
        lowerKC = new DifferenceIndicator(ma, new MultiplierIndicator(rangema, multKC));

        val = new SimpleLinearRegressionIndicator(src  -  avg(avg(highest(high, len1), lowest(low, len1)),sma(close,len1)), len1,0);
        */

        //Decimal preClose = index == 0 ? Decimal.ZERO : getTimeSeries().getTick(index - 1).getClosePrice();

        //Indicator<Decimal> range = useTrueRange ? max(high.minus(low), high.minus(preClose), low.minus(preClose)) : high.minus(low);
        //SMAIndicator rangema = new SMAIndicator(range, len1);
        //Indicator<Decimal> upperKC = new PlusIndicator(ma, new MultiplierIndicator(rangema, multKC));
        //Indicator<Decimal> lowerKC = new DifferenceIndicator(ma, new MultiplierIndicator(rangema, multKC));

        ClosePriceIndicator closePrice = new ClosePriceIndicator(timeSeries);
        //HighestValueIndicator h = new HighestValueIndicator();
        //val = new SimpleLinearRegressionIndicator(src  -  avg(avg(highest(high, len1), lowest(low, len1)),new SMAIndicator(closePrice,len1)), len1,0);
    }

    @Override
    protected Decimal calculate(int index) {
        Tick tick = getTimeSeries().getTick(index);

        //Decimal high = tick.getMaxPrice();
        //Decimal low = tick.getMinPrice();
        //Decimal preClose = index == 0 ? Decimal.ZERO : getTimeSeries().getTick(index - 1).getClosePrice();

        //Indicator<Decimal> range = useTrueRange ? max(high.minus(low), high.minus(preClose), low.minus(preClose)) : high.minus(low);
        //SMAIndicator rangema = new SMAIndicator(range, len1);
        //Indicator<Decimal> upperKC = new PlusIndicator(ma, new MultiplierIndicator(rangema, multKC));
        //Indicator<Decimal> lowerKC = new DifferenceIndicator(ma, new MultiplierIndicator(rangema, multKC));

        //HighestValueIndicator h = new HighestValueIndicator()
        //Indicator<Decimal> val = new SimpleLinearRegressionIndicator(src  -  avg(avg(highest(high, len1), lowest(low, len1)),sma(close,len1)), len1,0);

        //return val.getValue(index);
        return null;
    }
}
