package kr.kuvh.linebot.altcoin.bot.trading;

import kr.kuvh.linebot.altcoin.bot.common.ExchangeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Trader implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ExchangeEnum exchange;

    public Trader(ExchangeEnum exchange) {
        this.exchange = exchange;
    }

    @Override
    public void run() {
        init();
    }

    protected void init() {

    }

    protected abstract void cycle();
    protected abstract void chartAnalyze();

    public ExchangeEnum getExchange() {
        return this.exchange;
    }

}
