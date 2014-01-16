package trading.api_interfaces;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import bitcoinGWT.shared.model.TickerShallowObject;
import bitcoinGWT.shared.model.TradesFullLayoutObject;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/12/14
 * Time: 12:07 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class MtGoxTradeInterface implements TradeInterface {

    @Override
    public <T extends TickerShallowObject> T getLastPrice(Markets market, Currency cur) {
        return getLastPrice(cur);
    }

    @Override
    public <T extends TickerShallowObject> T getPrice(Markets market, Currency currency) {
        return getPrice(currency);
    }

    @Override
    public List<TradesFullLayoutObject> getTrades(Markets market, Currency currency, long previousTimestamp) {
        return getTrades(currency, previousTimestamp);
    }

    @Override
    public Set<Currency> getSupportedCurrencies(Markets market) {
        Set<Currency> result = new LinkedHashSet<>();
        result.add(Currency.EUR);
        return result;
    }

    @Override
    public String getLag(Markets market) {
        return getLag();
    }

    /**
     * Returns the current price of 1 BTC in given currency.
     * @return      a double value with the current price of 1 BTC in the Currency cur
     */
    public abstract <T extends TickerShallowObject> T getLastPrice(Currency cur);
    public abstract <T extends TickerShallowObject> T getPrice(Currency currency);
    public abstract List<TradesFullLayoutObject> getTrades(Currency currency, long previousTimestamp);
    public abstract String getLag();
}
