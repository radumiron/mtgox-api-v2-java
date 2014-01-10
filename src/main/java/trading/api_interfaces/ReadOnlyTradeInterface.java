package trading.api_interfaces;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TickerShallowObject;
import bitcoinGWT.shared.model.TradesFullLayoutObject;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/24/13
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ReadOnlyTradeInterface {

    /**
     * Returns an array with the user's balance in different currencies.
     *
     * The first element of the array is the amount in BTC,
     * the second element in USD and the third in EUR.
     * @return      an array with the current balance
     */
    public double[] getBalance() ;


    /**
     * Returns the current price of 1 BTC in given currency.
     * @return      a double value with the current price of 1 BTC in the Currency cur
     */
    public <T extends TickerShallowObject> T getLastPrice(Currency cur);

    public <T extends TickerShallowObject> T getPrice(Currency currency);


    /**
     * Returns the lag of the trading engine.
     * @return      a string with the lag
     */
    public String getLag();

    public List<TradesFullLayoutObject> getTrades(Currency currency, long previousTimestamp);
}
