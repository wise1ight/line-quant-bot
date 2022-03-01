package kr.kuvh.linebot.line;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import eu.verdelhan.ta4j.Tick;
import eu.verdelhan.ta4j.TimeSeries;
import eu.verdelhan.ta4j.indicators.helpers.ClosePriceIndicator;
import kr.kuvh.linebot.altcoin.api.coinone.CoinoneApi;
import kr.kuvh.linebot.altcoin.bot.common.ExchangeEnum;
import kr.kuvh.linebot.annotation.ChatCommand;
import kr.kuvh.linebot.altcoin.bot.util.TimeSeriesUtil;
import kr.kuvh.linebot.altcoin.poloniex.bot.TradeApi;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiCompleteBalanceMessage;
import kr.kuvh.linebot.altcoin.poloniex.message.ApiMessage.ApiVolumeMessage;
import kr.kuvh.linebot.annotation.ChatParam;
import kr.kuvh.linebot.annotation.LineUserId;
import kr.kuvh.linebot.util.Time;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.DefaultHighLowDataset;
import org.jfree.data.xy.OHLCDataset;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.util.*;
import java.util.List;
import java.util.concurrent.ExecutionException;

@LineMessageHandler
public class ChatController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private ApplicationContext appContext;
    private LineMessagingClient lineMessagingClient;
    private HashMap<String, Method> commands;

    @Autowired
    public ChatController(ApplicationContext appContext, LineMessagingClient lineMessagingClient) {
        this.appContext = appContext;
        this.lineMessagingClient = lineMessagingClient;
        this.commands = new HashMap<>();
    }

    @PostConstruct
    private void loadCommands() {
        Reflections reflections  = new Reflections(new MethodAnnotationsScanner(), "kr.kuvh.linebot");
        Set<Method> methods = reflections.getMethodsAnnotatedWith(ChatCommand.class);
        for(Method method : methods) {
            ChatCommand command = method.getAnnotation(ChatCommand.class);
            String[] commandArr = command.command().split("\\s");
            commands.put(commandArr[0], method);
            logger.info(commandArr[0] + " 명령어 맵핑 완료.");
        }
    }

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        TextMessageContent message = event.getMessage();
        handleTextContent(event.getReplyToken(), event, message);
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content)
            throws Exception {
        String text = content.getText();
        String lineUserId = event.getSource().getUserId();
        String[] sub = text.split("\\s");

        if (sub.length >= 1 && commands.containsKey(sub[0])) {
            Method method = commands.get(sub[0]);
            ChatCommand command = method.getAnnotation(ChatCommand.class);
            String[] comsub = command.command().split("\\s");
            if(comsub.length != sub.length) {
                this.replyText(replyToken, "해당 명령어의 사용법은 '" + command.help() + "' 입니다.");
                return;
            }
            HashMap<String, String> paramMap = new HashMap<>();
            for(int i = 1; i < comsub.length; i++) {
                paramMap.put(comsub[i], sub[i]);
            }

            Object obj = appContext.getBean(method.getDeclaringClass());
            ArrayList<Object> p = new ArrayList<>();
            Annotation[][] parameterAnnotations = method.getParameterAnnotations();
            for(Annotation[] annotations : parameterAnnotations) {
                for (Annotation annotation : annotations) {
                    if(annotation instanceof LineUserId) {
                        p.add(lineUserId);
                    } else if (annotation instanceof ChatParam) {
                        p.add(paramMap.get(((ChatParam) annotation).value()));
                    }
                }
            }

            this.reply(replyToken, (Message) method.invoke(obj, p.toArray()));
        } else if (sub.length >= 1 && sub[0].equals("도움말")) {
            StringBuilder sb = new StringBuilder("사용할 수 있는 명령어 목록 입니다.");
            for(Map.Entry<String, Method> entry : commands.entrySet()) {
                ChatCommand command = entry.getValue().getAnnotation(ChatCommand.class);
                sb.append("\n").append(command.help());
            }
            this.replyText(replyToken, sb.toString());
        } else {
            this.replyText(replyToken, "사용할 수 없는 명령어입니다. '도움말' 명령어를 통해 사용할 수 있는 명령어를 확인해 보세요.");
        }

        /*
        if (sub.length >= 1) {
            switch (sub[0]) {
                case "비트시세":
                    this.replyText(replyToken, "비트코인 시세는 1BTC = "
                            + CoinoneApi.getInstance().getBitcoinQuote() + "￦ 입니다.");
                    break;
                case "자산":
                    this.replyText(replyToken, getAssetMessage());
                    break;
                case "볼륨":
                    this.replyText(replyToken, getVolumeMessage());
                    break;
                case "비트잔고":
                    HashMap<String, BigDecimal> balances = TradeApi.getInstance().returnBalances();
                    if (balances != null)
                        this.replyText(replyToken, balances.get("BTC") + "가짐");
                    break;
                case "차트":
                    String uri = getChartImageUri(sub[1]);
                    this.reply(replyToken, new ImageMessage(uri, uri));
                    break;
                case "회원가입":
                    List<Member> members = memberService.findMemberByLineUserId(lineUserId);
                    this.replyText(replyToken, String.valueOf(members.size()));
                    break;
                default:

                    break;
            }
        } else {
            this.replyText(replyToken, "오류가 발생하였습니다.");
        }
        */
    }

    private void reply(String replyToken, Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(String replyToken, List<Message> messages) {
        try {
            BotApiResponse apiResponse = lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messages))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void replyText(String replyToken, String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken must not be empty");
        }

        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "……";
        }

        this.reply(replyToken, new TextMessage(message));
    }

    private String getAssetMessage() {
        HashMap<String, ApiCompleteBalanceMessage> balance = TradeApi.getInstance().returnCompleteBalances();
        BigDecimal btcAmount = BigDecimal.ZERO;

        StringBuilder sb = new StringBuilder("현재 보유중인 알트코인 내역입니다.\n\n종목 | 총량 | 주문 | BTC");
        for (Map.Entry<String, ApiCompleteBalanceMessage> entry : balance.entrySet()) {
            if (entry.getValue().getAvailable().signum() != 0) {
                sb.append(String.format("\n%s | %s | %s | %s", entry.getKey(), entry.getValue().getAvailable(), entry.getValue().getOnOrders(), entry.getValue().getBtcValue()));
                btcAmount = btcAmount.add(entry.getValue().getBtcValue());
            }
        }
        sb.append("\n\n총 자산은 ").append((long) Math.floor(CoinoneApi.getInstance().getBitcoinQuote() * btcAmount.doubleValue())).append("원 입니다.");

        return sb.toString();
    }

    private String getVolumeMessage() {
        ApiVolumeMessage message = TradeApi.getInstance().return24Volume();
        StringBuilder sb = new StringBuilder("볼륨...");
        sb.append(message.getProperties().get("BTC_ETH").get("BTC"));
        sb.append(" ").append(message.getTotalBTC());
        sb.append(" 이네...");

        return sb.toString();
    }



}
