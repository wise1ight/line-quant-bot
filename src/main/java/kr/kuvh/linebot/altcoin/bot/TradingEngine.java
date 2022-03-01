package kr.kuvh.linebot.altcoin.bot;

import kr.kuvh.linebot.altcoin.bot.common.BitcoinMarketEnum;
import kr.kuvh.linebot.altcoin.bot.common.ExchangeEnum;
import kr.kuvh.linebot.altcoin.bot.trading.MarginTrader;
import kr.kuvh.linebot.altcoin.bot.trading.NormalTrader;
import kr.kuvh.linebot.util.MessageLoggerUtil;
import kr.kuvh.linebot.altcoin.bot.watchdog.BitcoinWatchdog;
import kr.kuvh.linebot.altcoin.bot.watchdog.BittrexChartWatchdog;
import org.knowm.xchange.currency.CurrencyPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@ComponentScan(basePackages = {"kr.kuvh.linebot"})
public class TradingEngine implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final TaskExecutor taskExecutor;
    private volatile boolean keepAlive = true;
    private boolean isRunning = false;
    private static final Object IS_RUNNING_MONITOR = new Object();
    private Thread engineThread;

    private BitcoinMarketEnum bitcoinMarketStatus = BitcoinMarketEnum.INIT;
    private BitcoinMarketEnum bitcoinMarketTrend = BitcoinMarketEnum.INIT;
    private ConcurrentHashMap<String, NormalTrader> normalTraders;
    private ConcurrentHashMap<String, MarginTrader> marginTraders;

    @Autowired
    public TradingEngine(TaskExecutor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        start();
    }

    public void start() throws IllegalStateException {

        synchronized (IS_RUNNING_MONITOR) {
            if (isRunning) {
                MessageLoggerUtil.pushMessage(logger, "트레이딩 엔진이 이미 실행 중 입니다.");
                throw new IllegalStateException();
            }

            isRunning = true;
        }

        engineThread = Thread.currentThread();

        initWatchdogs();
        runMainLoop();
    }

    private void initWatchdogs() {
        taskExecutor.execute(new BitcoinWatchdog(this));
        taskExecutor.execute(new BittrexChartWatchdog(this));
    }

    private void runMainLoop() {
        MessageLoggerUtil.pushMessage(logger, "트레이딩 엔진을 시작합니다.");

        while (keepAlive) {
            try {
                try {
                    Thread.sleep(500);
                    //한 사이클을 도는 동안 와치독과 트레이더와 기타등등이 정상적으로 작동하는지 체크해 주고
                    //죽었다면 다시 살려내야함
                } catch (InterruptedException e) {
                    MessageLoggerUtil.pushMessage(logger, "트레이딩 엔진을 종료합니다.");
                    Thread.currentThread().interrupt();
                }
            } catch (Exception e) {
                MessageLoggerUtil.pushErrorMessage(logger, "예외 : " + e.getMessage());
            }
        }

        synchronized (IS_RUNNING_MONITOR) {
            isRunning = false;
            MessageLoggerUtil.pushMessage(logger, "트레이딩 엔진이 종료되었습니다.");
        }
    }

    public void stop() {
        keepAlive = false;
        engineThread.interrupt();
    }

    public BitcoinMarketEnum getBitcoinMarketStatus() {
        return this.bitcoinMarketStatus;
    }

    public void setBitcoinMarketStatus(BitcoinMarketEnum e) {
        if(bitcoinMarketStatus != e) {
            bitcoinMarketStatus = e;
            switch (e) {
                case CROSSBAR:
                    MessageLoggerUtil.pushMessage(logger, "비트코인 횡보장");
                    break;
                case NOT_CROSSBAR:
                    MessageLoggerUtil.pushMessage(logger, "비트코인 횡보장 아님");
                    break;
            }
        }
    }

    public BitcoinMarketEnum getBitcoinMarketTrend() {
        return this.bitcoinMarketTrend;
    }

    public void setBitcoinMarketTrend(BitcoinMarketEnum e) {
        if(bitcoinMarketTrend != e) {
            bitcoinMarketTrend = e;
            switch (e) {
                case UPPER:
                    MessageLoggerUtil.pushMessage(logger, "비트코인 상승장");
                    break;
                case LOWER:
                    MessageLoggerUtil.pushMessage(logger, "비트코인 하락장");
                    break;
            }
        }
    }

    public synchronized boolean isRunning() {
        return this.isRunning;
    }

    public void createNormalTrader(ExchangeEnum exchangeName, CurrencyPair currencyPair) {
        MessageLoggerUtil.pushMessage(logger, exchangeName.name() + "에서 " + currencyPair + "구매 찬스");
        //TODO 장 상황 분석 후 알트 거래 할지 말지도 체크해야함..
        //기본적으로 비트가 횡보가 아닐경우에는 거래 세션을 새로 열면 안됨..
        //기존에 열려있던건 익절칠 수 있는건 익절 빤스런
        if(!hasNormalTrader(exchangeName, currencyPair)) {
            NormalTrader trader = new NormalTrader(exchangeName);
            taskExecutor.execute(trader);
            normalTraders.put(exchangeName.name() + ":" + currencyPair.toString(), trader);
        }
    }

    public boolean hasNormalTrader(ExchangeEnum exchangeName, CurrencyPair currencyPair) {
        return normalTraders.containsKey(exchangeName.name() + ":" + currencyPair.toString());
    }

}
