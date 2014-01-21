package bitcoinGWT.server.ticker;

import org.apache.log4j.Logger;
import trading.api_interfaces.TradeInterface;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/25/13
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractTradeEngine {

    private static final Logger LOG = Logger.getLogger(AbstractTradeEngine.class);

    protected static int INITIAL_DELAY = 5000;

    private Timer tickerTimer = new Timer(getTradeName());

    @PostConstruct
    private void init() {
        LOG.info("Initializing AbstractTradeEngine for trade: " + getTradeName());
        tickerTimer.scheduleAtFixedRate(new TickerTask(), getInitialDelay(), getTimerInterval());
    }

    protected abstract void executeTradeTask();
    protected abstract int getTimerInterval();
    protected abstract String getTradeName();

    protected int getInitialDelay() {
        return INITIAL_DELAY;
    }

    class TickerTask extends TimerTask {

        @Override
        public void run() {
            LOG.info("Running timer task for trade engine: " + getTradeName());
            executeTradeTask();
        }
    }
}
