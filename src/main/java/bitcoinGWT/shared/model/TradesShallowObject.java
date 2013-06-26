package bitcoinGWT.shared.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/25/13
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesShallowObject {
    protected Date date;
    protected double price;
    protected double amount;


    public TradesShallowObject(Date date, double tradePrice, double tradeAmount) {
        this.date = date;
        this.price = tradePrice;
        this.amount = tradeAmount;
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
        return "TradesShallowObject{" +
                "amount=" + amount +
                ", date=" + date +
                ", price=" + price +
                '}';
    }
}



