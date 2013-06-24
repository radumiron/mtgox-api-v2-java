package bitcoinGWT.shared.model;

import mtgox_api.com.mtgox.api.MtGox;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/22/13
 * Time: 7:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TickerFullLayoutObject extends TickerShallowObject {

    public TickerFullLayoutObject(MtGox.Currency currency, double price) {
        super(currency, price);
    }
}
