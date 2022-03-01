package kr.kuvh.linebot.altcoin.poloniex.client;

import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiErrorMessage;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Response;

import java.io.IOException;
import java.lang.annotation.Annotation;

/**
 * Created by kuvh on 2017-04-12.
 */
public class ErrorUtils {
    public static ApiErrorMessage parseError(Response<?> response) {
        Converter<ResponseBody, ApiErrorMessage> converter =
                ApiClient.getInstance().tradingRetrofit
                        .responseBodyConverter(ApiErrorMessage.class, new Annotation[0]);

        ApiErrorMessage error;

        try {
            error = converter.convert(response.errorBody());
        } catch (IOException e) {
            return new ApiErrorMessage();
        }

        return error;
    }
}
