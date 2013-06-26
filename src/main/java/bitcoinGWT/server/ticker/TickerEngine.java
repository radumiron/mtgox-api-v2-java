package bitcoinGWT.server.ticker;

import mtgox_api.com.mtgox.api.MtGox;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/19/13
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TickerEngine extends AbstractTradeEngine {

    protected static int TICKER_INTERVAL = 100;

    @Override
    protected void executeTradeTask() {
        Date initialDate = new Date();
        System.out.println(initialDate + ": execute ticker task");
        double price = trade.getLastPrice(MtGox.Currency.EUR).getPrice();
        //String lag = trade.getLag();
        System.out.println(new Date() + ": last price: " + price);// + ", lag: " + lag);
        System.out.println();
    }

    @Override
    protected int getTimerInterval() {
        return TICKER_INTERVAL;
    }


}
