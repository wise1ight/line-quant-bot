package kr.kuvh.linebot.altcoin.bot.watchdog;

public abstract class Watchdog implements Runnable {

    private int cycleMillis = 5000;

    @Override
    public void run() {
        while(true) {
            runCycle();

            try {
                Thread.sleep(cycleMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected void setCycleMillis(int cycleMillis) {
        this.cycleMillis = cycleMillis;
    }

    protected abstract void runCycle();
}
