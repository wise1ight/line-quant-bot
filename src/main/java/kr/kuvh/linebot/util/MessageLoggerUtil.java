package kr.kuvh.linebot.util;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.PushMessage;
import com.linecorp.bot.model.message.TextMessage;
import kr.kuvh.linebot.ApiKeys;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageLoggerUtil {

    private static LineMessagingClient lineMessagingClient;

    @Autowired
    public MessageLoggerUtil(LineMessagingClient lineMessagingClient) {
        MessageLoggerUtil.lineMessagingClient = lineMessagingClient;
    }

    public static void pushMessage(Logger logger, String msg) {
        logger.info(msg);
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));
    }

    public static void pushWarnMessage(Logger logger, String msg) {
        logger.warn(msg);
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage("[경고]" + msg)));
    }

    public static void pushErrorMessage(Logger logger, String msg) {
        logger.error(msg);
        lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage("[오류]" + msg)));
    }
}
