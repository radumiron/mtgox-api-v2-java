package bitcoinGWT.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/27/13
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChartElement implements IsSerializable {
    private double high;
    private double low;
    private double open;
    private double close;
    private double amount;

    private TimeWindow elementDate;

    private TimeInterval timeInterval;
    private Date timeOfLastTrade;

    //todo add trade items to the chart element - in case a user select the chart element, he'll see the actual trades

    public ChartElement() {
    }

    public ChartElement(double open, double close, double low, double high, double amount, TimeWindow elementDate,
                        Date timeOfLastTrade, TimeInterval timeInterval) {
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
        this.elementDate = elementDate;
        this.timeInterval = timeInterval;
        this.amount = amount;
        this.timeOfLastTrade = timeOfLastTrade;
    }

    public double getClose() {
        return close;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getOpen() {
        return open;
    }

    public TimeInterval getTimeInterval() {
        return timeInterval;
    }

    public TimeWindow getElementDate() {
        return elementDate;
    }

    public double getAmount() {
        return amount;
    }

    public Date getTimeOfLastTrade() {
        return timeOfLastTrade;
    }

    @Override
    public String toString() {
        return "ChartElement{" +
                "amount=" + amount +
                ", high=" + high +
                ", low=" + low +
                ", open=" + open +
                ", close=" + close +
                ", elementDate=" + elementDate +
                ", timeInterval=" + timeInterval +
                ", timeOfLastTrade=" + timeOfLastTrade +
                '}';
    }
}
