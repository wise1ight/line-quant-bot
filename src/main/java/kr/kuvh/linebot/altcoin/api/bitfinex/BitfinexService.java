package kr.kuvh.linebot.altcoin.api.bitfinex;

import kr.kuvh.linebot.ApiKeys;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bitfinex.v1.BitfinexExchange;

public class BitfinexService {

    public static Exchange getExchange() {
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(BitfinexExchange.class);
        exchangeSpecification.setApiKey(ApiKeys.BITFINEX_KEY);
        exchangeSpecification.setSecretKey(ApiKeys.BITFINEX_SECRET);

        return ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
    }
}
