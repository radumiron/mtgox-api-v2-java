package bitcoinGWT.server.ticker;

import mtgox_api.com.mtgox.api.MtGox;
import mtgox_api.com.mtgox.api.TradeInterface;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/25/13
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTradeEngine {

    protected static int INITIAL_DELAY = 5000;

    private Timer tickerTimer = new Timer();

    @Autowired
    protected TradeInterface trade;

    protected AbstractTradeEngine() {
        tickerTimer.scheduleAtFixedRate(new TickerTask(), getInitialDelay(), getTimerInterval());
    }

    protected abstract void executeTradeTask();
    protected abstract int getTimerInterval();

    protected int getInitialDelay() {
        return INITIAL_DELAY;
    }

    class TickerTask extends TimerTask {

        @Override
        public void run() {
            executeTradeTask();
        }
    }
}
