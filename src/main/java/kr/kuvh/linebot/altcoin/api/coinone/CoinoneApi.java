package kr.kuvh.linebot.altcoin.api.coinone;

import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class CoinoneApi {

    private static CoinoneApi instance;
    private RestTemplate restTemplate;
    private String url = "https://api.coinone.co.kr/trades/";

    private CoinoneApi() {
        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        List<HttpMessageConverter<?>> messageConverterList = restTemplate.getMessageConverters();

        MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();

        List<MediaType> supportedMediaTypes = new ArrayList<MediaType>();
        supportedMediaTypes.add(new MediaType("text", "plain"));
        supportedMediaTypes.add(new MediaType("application", "json"));
        jsonMessageConverter.setSupportedMediaTypes(supportedMediaTypes);
        messageConverterList.add(jsonMessageConverter);
        restTemplate.setMessageConverters(messageConverterList);
    }

    public static CoinoneApi getInstance() {
        if (instance == null) {
            instance = new CoinoneApi();
        }
        return instance;
    }

    public int getBitcoinQuote() {
        TradesApiMessage message = restTemplate.getForObject(url, TradesApiMessage.class);

        return message.getCompleteOrders().get(0).getPrice();
    }
}
