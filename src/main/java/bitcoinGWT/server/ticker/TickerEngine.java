package bitcoinGWT.server.ticker;

import bitcoinGWT.shared.model.Constants;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TickerFullLayoutObject;
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

    private TickerFullLayoutObject fullLayoutObject;

    @Override
    protected void executeTradeTask() {
        Date initialDate = new Date();
        System.out.println(initialDate + ": execute ticker task");
        //double price = trade.getPrice(MtGox.Currency.EUR).getPrice();
        fullLayoutObject = trade.getPrice(Currency.EUR);
        double price = fullLayoutObject.getPrice();
        //String lag = trade.getLag();
        System.out.println(new Date() + ": last price: " + price);// + ", lag: " + lag);
        System.out.println();
    }

    public TickerFullLayoutObject getPrice(Currency currency) {
        return fullLayoutObject;
    }

    @Override
    protected int getTimerInterval() {
        return Constants.TICKER_INTERVAL;
    }


}
