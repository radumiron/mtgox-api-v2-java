package bitcoinGWT.shared.model;

import mtgox_api.com.mtgox.api.MtGox;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/25/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesFullLayoutObject extends TradesShallowObject  {
    //{"date":1364767201,"price":"92.65","amount":"0.47909825","price_int":"9265000","amount_int":"47909825","tid":"1364767201381791"
    // ,"price_currency":"USD","item":"BTC","trade_type":"bid","primary":"Y","pro
    private MtGox.Currency currency;
    private MtGox.Currency tradeItem;
    private TradeType type;

    public TradesFullLayoutObject(Date dateDate, double tradePrice, double tradeAmount, MtGox.Currency currency, MtGox.Currency tradeItem, TradeType type) {
        super(dateDate, tradePrice, tradeAmount);
        this.currency = currency;
        this.tradeItem = tradeItem;
        this.type = type;
    }

    public MtGox.Currency getCurrency() {
        return currency;
    }

    public MtGox.Currency getTradeItem() {
        return tradeItem;
    }

    public TradeType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TradesFullLayoutObject{" +
                "currency=" + currency +
                ", tradeItem=" + tradeItem +
                ", type=" + type +
                "} " + super.toString();
    }
}
