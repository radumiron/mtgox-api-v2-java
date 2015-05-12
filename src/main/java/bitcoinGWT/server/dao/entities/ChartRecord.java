package bitcoinGWT.server.dao.entities;

import bitcoinGWT.shared.model.TimeInterval;
import bitcoinGWT.shared.model.TimeWindow;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 2/10/14
 * Time: 10:46 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChartRecord {
    //high
    public static final String COLUMN_HIGH = "h";
    //low
    public static final String COLUMN_LOW = "l";
    //open
    public static final String COLUMN_OPEN = "o";
    //close
    public static final String COLUMN_CLOSE = "c";
    //amount
    public static final String COLUMN_AMOUNT = "a";
    //element date
    public static final String COLUMN_ELEMENT_DATE = "e_d";
    //time interval
    public static final String COLUMN_TIME_INTERVAL = "t_i";
    //time of last trade
    public static final String COLUMN_TIME_OF_LAST_TRADE = "t_l_t";

    private double high;
    private double low;
    private double open;
    private double close;
    private double amount;

    private TimeWindow elementDate;

    private TimeInterval timeInterval;
    private Date timeOfLastTrade;

    public ChartRecord(double amount, double close, TimeWindow elementDate, double high, double low, double open, TimeInterval timeInterval, Date timeOfLastTrade) {
        this.amount = amount;
        this.close = close;
        this.elementDate = elementDate;
        this.high = high;
        this.low = low;
        this.open = open;
        this.timeInterval = timeInterval;
        this.timeOfLastTrade = timeOfLastTrade;
    }

    public double getAmount() {
        return amount;
    }

    public double getClose() {
        return close;
    }

    public TimeWindow getElementDate() {
        return elementDate;
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

    public Date getTimeOfLastTrade() {
        return timeOfLastTrade;
    }
}
