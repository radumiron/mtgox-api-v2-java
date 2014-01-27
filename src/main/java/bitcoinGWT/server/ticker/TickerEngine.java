package bitcoinGWT.server.ticker;

import bitcoinGWT.shared.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import trading.api_interfaces.MtGoxTradeInterface;
import trading.api_interfaces.TradeInterface;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/19/13
 * Time: 12:07 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
public abstract class TickerEngine extends AbstractTradeEngine {

    @Autowired
    private TradeInterface trade;

    private TickerFullLayoutObject fullLayoutObject;

    @Override
    protected void executeTradeTask() {
        /*Date initialDate = new Date();
        System.out.println(initialDate + ": execute ticker task");
        //double price = trade.getPrice(MtGox.Currency.EUR).getPrice();
        TickerShallowObject tradeObject = trade.getPrice(Markets.MTGOX, Currency.EUR);
        if (tradeObject instanceof TickerFullLayoutObject) {
            //if all went well, we should have a full layout object
            fullLayoutObject = (TickerFullLayoutObject) tradeObject;
            double price = fullLayoutObject.getPrice();
            //String lag = trade.getLag();
            System.out.println(new Date() + ": last price: " + price);// + ", lag: " + lag);
            System.out.println();
        } else {
            System.out.println(new Date() + ": something went wrong when getting the price. Got a shallow object instead of a full object");
            //String lag = trade.getLag();
            System.out.println(new Date() + ": last price: " + tradeObject.getPrice());// + ", lag: " + lag);
            System.out.println();
        }*/
    }

    public TickerFullLayoutObject getPrice(Markets market, Currency currency) {
        return fullLayoutObject;
    }

    @Override
    protected int getTimerInterval() {
        return Constants.TICKER_INTERVAL;
    }

    protected String getTradeName() {
        return this.getClass().getName();
    }


}
