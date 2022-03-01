package kr.kuvh.linebot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * Created by kuvh on 2017-04-08.
 */
@Service
public class ApiKeys {
    //LINE
    @Value("${api.line.key}")
    private String lineUserKey;
    public static String LINE_USER_KEY;

    //POLONIEX
    @Value("${api.poloniex.key}")
    private String poloniexKey;
    public static String POLONIEX_KEY;

    @Value("${api.poloniex.secret}")
    private String poloniexSecret;
    public static String POLONIEX_SECRET;

    //BITHUMB
    @Value("${api.bithumb.connect}")
    private String bithumbConnect;
    public static String BITHUMB_CONNECT_KEY;

    @Value("${api.bithumb.secret}")
    private String bithumbSecret;
    public static String BITHUMB_SECRET_KEY;

    //BITFINEX
    @Value("${api.bitfinex.key}")
    private String bitfinexKey;
    public static String BITFINEX_KEY;

    @Value("${api.bitfinex.secret}")
    private String bitfinexSecret;
    public static String BITFINEX_SECRET;

    //BITTREX
    @Value("${api.bittrex.key}")
    private String bittrexKey;
    public static String BITTREX_KEY;

    @Value("${api.bittrex.secret}")
    private String bittrexSecret;
    public static String BITTREX_SECRET;

    @PostConstruct()
    private void setProperty() {
        LINE_USER_KEY = lineUserKey;
        POLONIEX_KEY = poloniexKey;
        POLONIEX_SECRET = poloniexSecret;
        BITHUMB_CONNECT_KEY = bithumbConnect;
        BITHUMB_SECRET_KEY = bithumbConnect;
        BITFINEX_KEY = bitfinexKey;
        BITFINEX_SECRET = bitfinexSecret;
        BITTREX_KEY = bittrexKey;
        BITTREX_SECRET = bittrexSecret;
    }
}
