package bitcoinGWT.client.events;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/17/14
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class CurrencyChangeEvent extends GwtEvent<CurrencyChangeEventHandler> {

    public static Type<CurrencyChangeEventHandler> TYPE = new Type<CurrencyChangeEventHandler>();

    private Markets market;
    private Currency currency;

    public CurrencyChangeEvent(Markets market, Currency currency) {
        this.currency = currency;
        this.market = market;
    }

    public Currency getCurrency() {
        return currency;
    }

    public Markets getMarket() {
        return market;
    }

    @Override
    public Type<CurrencyChangeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(CurrencyChangeEventHandler handler) {
        handler.onCurrencyChanged(this);
    }
}
