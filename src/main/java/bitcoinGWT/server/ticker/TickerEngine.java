package bitcoinGWT.server.ticker;

import mtgox_api.com.mtgox.api.MtGox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/19/13
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TickerEngine {

    @Autowired
    MtGox trade;

    private static int INITIAL_DELAY = 1000;
    private static int TICKER_INTERVAL = 500;

    private Timer tickerTimer = new Timer();

    public TickerEngine() {
        tickerTimer.scheduleAtFixedRate(new TickerTask(), INITIAL_DELAY, TICKER_INTERVAL);
    }

    class TickerTask extends TimerTask {

        @Override
        public void run() {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
