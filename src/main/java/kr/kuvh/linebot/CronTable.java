package kr.kuvh.linebot;

import kr.kuvh.linebot.altcoin.bot.TradingEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by kuvh on 2017-04-08.
 */
@Component
public class CronTable {

    @Autowired
    private TradingEngine tradingEngine;

    @Scheduled(cron="0 0 * * * *")
    public void analysis24h() {
        /*
        try {
            BigDecimal lastBTC = (BigDecimal)dbService.getLastBTCAmount().get(0).get("amount");
            HashMap<String, ApiCompleteBalanceMessage> balance = TradeApi.getInstance().returnCompleteBalances();
            BigDecimal currentBTC = BigDecimal.ZERO;
            for (Map.Entry<String, ApiCompleteBalanceMessage> entry : balance.entrySet()) {
                if (entry.getValue().getAvailable().signum() != 0) {
                    currentBTC = currentBTC.add(entry.getValue().getBtcValue());
                }
            }

            BigDecimal ONE_HUNDRED = new BigDecimal(100);
            BigDecimal percent = currentBTC.subtract(lastBTC).divide(lastBTC, BigDecimal.ROUND_DOWN).multiply(ONE_HUNDRED);

            String msg = "현재 보유중인 BTC는 " + currentBTC.toString() + "BTC 입니다.\n 한시간 전 대비 " + percent.toString() + "% " + ((percent.compareTo(BigDecimal.ZERO) > 0) ? "증가" : "하락") + " 하였습니다.";
            lineMessagingClient.pushMessage(new PushMessage(ApiKeys.LINE_USER_KEY, new TextMessage(msg)));

            Map<String, Object> query = new HashMap<>();
            query.put("amount", currentBTC);
            query.put("timestamp", Time.getCurrentTimestamp());
            dbService.insertLastBTCAmount(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
    }
}
