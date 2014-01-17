package bitcoinGWT.client.events;

import com.google.gwt.event.shared.EventHandler;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/17/14
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
public interface CurrencyChangeEventHandler extends EventHandler {
    void onCurrencyChanged(CurrencyChangeEvent currencyChangeEvent);
}