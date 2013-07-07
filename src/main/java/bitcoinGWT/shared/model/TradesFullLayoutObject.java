package bitcoinGWT.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;
import mtgox_api.com.mtgox.api.MtGox;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/25/13
 * Time: 9:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesFullLayoutObject implements IsSerializable {
    //{"date":1364767201,"price":"92.65","amount":"0.47909825","price_int":"9265000","amount_int":"47909825","tid":"1364767201381791"
    // ,"price_currency":"USD","item":"BTC","trade_type":"bid","primary":"Y","pro
    private Currency currency;
    private Currency tradeItem;
    private TradeType type;
    protected Date date;
    protected double price;
    protected double amount;

    public TradesFullLayoutObject(Date dateDate, double tradePrice, double tradeAmount, Currency currency, Currency tradeItem, TradeType type) {
        this.date = dateDate;
        this.price = tradePrice;
        this.amount = tradeAmount;
        this.currency = currency;
        this.tradeItem = tradeItem;
        this.type = type;
    }

    public TradesFullLayoutObject() {
    }

    public Currency getCurrency() {
        return currency;
    }

    public Currency getTradeItem() {
        return tradeItem;
    }

    public TradeType getType() {
        return type;
    }

    public Date getDate() {
        return date;
    }

    public double getPrice() {
        return price;
    }

    public double getAmount() {
        return amount;
    }

    public enum TradeType {
        bid, ask
    }

    @Override
    public String toString() {
        return "TradesFullLayoutObject{" +
                "amount=" + amount +
                ", currency=" + currency +
                ", tradeItem=" + tradeItem +
                ", type=" + type +
                ", date=" + date +
                ", price=" + price +
                "} " + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradesFullLayoutObject)) return false;
        if (!super.equals(o)) return false;

        TradesFullLayoutObject that = (TradesFullLayoutObject) o;

        if (Double.compare(that.amount, amount) != 0) return false;
        if (Double.compare(that.price, price) != 0) return false;
        if (currency != that.currency) return false;
        if (!date.equals(that.date)) return false;
        if (tradeItem != that.tradeItem) return false;
        if (type != that.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + currency.hashCode();
        result = 31 * result + tradeItem.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + date.hashCode();
        temp = price != +0.0d ? Double.doubleToLongBits(price) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = amount != +0.0d ? Double.doubleToLongBits(amount) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
