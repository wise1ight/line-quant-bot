package kr.kuvh.linebot.altcoin.api.bittrex;

import kr.kuvh.linebot.ApiKeys;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.bittrex.BittrexExchange;

public class BittrexService {

    public static Exchange getExchange() {
        ExchangeSpecification exchangeSpecification = new ExchangeSpecification(BittrexExchange.class);
        exchangeSpecification.setApiKey(ApiKeys.BITTREX_KEY);
        exchangeSpecification.setSecretKey(ApiKeys.BITTREX_SECRET);

        return ExchangeFactory.INSTANCE.createExchange(exchangeSpecification);
    }
}
