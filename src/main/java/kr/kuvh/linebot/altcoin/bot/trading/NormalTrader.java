package kr.kuvh.linebot.altcoin.bot.trading;

import kr.kuvh.linebot.altcoin.bot.common.ExchangeEnum;

/*
Normal Trader : 일반 매수

주로 비트렉스에 있는 애들을 매수 매도
기술적 분석을 통해서 거래하는 알고리즘
 */

public class NormalTrader extends Trader {

    public NormalTrader(ExchangeEnum exchange) {
        super(exchange);
    }

    @Override
    protected void cycle() {

    }

    @Override
    protected void chartAnalyze() {

    }
}
