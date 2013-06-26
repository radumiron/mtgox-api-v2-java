package bitcoinGWT.shared.model;

import mtgox_api.com.mtgox.api.MtGox;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/22/13
 * Time: 7:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TickerShallowObject {

    private MtGox.Currency currency;
    private double price;

    public TickerShallowObject(MtGox.Currency currency, double price) {
        this.currency = currency;
        this.price = price;
    }

    public MtGox.Currency getCurrency() {
        return currency;
    }

    public double getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return "TickerShallowObject{" +
                "currency=" + currency +
                ", price=" + price +
                '}';
    }
}
