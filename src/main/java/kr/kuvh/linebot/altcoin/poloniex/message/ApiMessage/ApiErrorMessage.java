package kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by kuvh on 2017-04-09.
 */
public class ApiErrorMessage extends ApiMessage {

    @JsonProperty("error")
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
