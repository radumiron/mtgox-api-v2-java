package bitcoinGWT.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

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
    protected Double price;
    protected Double amount;
    protected Long tradeId;

    static double PRICE = 0;

    public TradesFullLayoutObject(long tradeId, Date dateDate, double tradePrice, double tradeAmount, Currency currency, Currency tradeItem, TradeType type) {
        this.tradeId = tradeId;
        this.date = dateDate;
        this.price = tradePrice;
        //this.price = ++PRICE;
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

    public Double getPrice() {
        return price;
    }

    public Double getAmount() {
        return amount;
    }

    public Long getTradeId() {
        return tradeId;
    }

    @Override
    public String toString() {
        return "TradesFullLayoutObject{" +
                "tradeId=" + tradeId +
                ", amount=" + amount +
                ", currency=" + currency +
                ", tradeItem=" + tradeItem +
                ", type=" + type +
                ", date=" + date +
                ", price=" + price +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradesFullLayoutObject)) return false;

        TradesFullLayoutObject that = (TradesFullLayoutObject) o;

        if (tradeId != that.tradeId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (tradeId ^ (tradeId >>> 32));
    }

    public enum TradeType {
        BID, ASK
    }
}
