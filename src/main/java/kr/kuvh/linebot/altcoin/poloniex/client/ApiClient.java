package kr.kuvh.linebot.altcoin.poloniex.client;

import kr.kuvh.linebot.util.Time;
import okhttp3.*;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Created by kuvh on 2017-04-12.
 */
public class ApiClient {

    private static ApiClient instance;
    public Retrofit tradingRetrofit;
    private PublicApiService publicApiService;
    private TradingApiService tradingApiService;

    private static int tempNonce = 0;
    private static long prevTimestamp = 0;

    public synchronized String getNonce() {
        long timestamp = Time.getCurrentTimestamp();

        if(prevTimestamp == timestamp) {
            tempNonce++;
        } else {
            prevTimestamp = timestamp;
            tempNonce = 0;
        }

        return String.valueOf(timestamp * 1000 + tempNonce);
    }

    protected ApiClient() {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(1);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();

                        Request.Builder requestBuilder = original.newBuilder();

                        if(original.body() instanceof FormBody){
                            FormBody.Builder newFormBody = new FormBody.Builder();
                            FormBody oidFormBody = (FormBody) original.body();
                            for (int i = 0;i<oidFormBody.size();i++){
                                newFormBody.addEncoded(oidFormBody.encodedName(i),oidFormBody.encodedValue(i));
                            }
                            newFormBody.add("nonce",getNonce());
                            requestBuilder.method(original.method(),newFormBody.build());
                        }

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }
                })
                .addNetworkInterceptor(new ApiInterceptor())
                .addInterceptor(new LoggingInterceptor())
                .dispatcher(dispatcher)
                .readTimeout(200, TimeUnit.SECONDS)
                .connectTimeout(200, TimeUnit.SECONDS)
                .build();

        Retrofit publicRetrofit = new Retrofit.Builder()
                .baseUrl("https://poloniex.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build();

        tradingRetrofit = new Retrofit.Builder()
                .baseUrl("https://poloniex.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();

        this.publicApiService = publicRetrofit.create(PublicApiService.class);
        this.tradingApiService = tradingRetrofit.create(TradingApiService.class);
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public PublicApiService getPublicApiService() {
        return publicApiService;
    }

    public TradingApiService getTradingApiService() {
        return tradingApiService;
    }
}
