package kr.kuvh.linebot.altcoin.api.bitfinex;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class BitfinexClient {

    private volatile static BitfinexClient instance;
    private BitfinexV2Service service;

    public static BitfinexClient getInstance() {
        if(instance == null) {
            instance = new BitfinexClient();
        }
        return instance;
    }

    private BitfinexClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.bitfinex.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        service = retrofit.create(BitfinexV2Service.class);
    }

    public BitfinexV2Service getService() {
        return service;
    }
}
