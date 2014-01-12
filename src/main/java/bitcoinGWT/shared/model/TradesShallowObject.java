package bitcoinGWT.shared.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/12/14
 * Time: 2:03 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesShallowObject {
    protected Date date;
    protected Double price;
    protected Double amount;

    public TradesShallowObject(Date date, Double amount, Double price) {
        this.amount = amount;
        this.date = date;
        this.price = price;
    }

    public TradesShallowObject() {
    }

    public Double getAmount() {
        return amount;
    }

    public Date getDate() {
        return date;
    }

    public Double getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradesShallowObject)) return false;

        TradesShallowObject that = (TradesShallowObject) o;

        if (amount != null ? !amount.equals(that.amount) : that.amount != null) return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = date != null ? date.hashCode() : 0;
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        return result;
    }
}
