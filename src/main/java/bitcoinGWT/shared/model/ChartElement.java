package bitcoinGWT.shared.model;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/27/13
 * Time: 8:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChartElement {
    private double high;
    private double low;
    private double open;
    private double close;

    private TimeInterval timeInterval;

    public ChartElement(double close, double high, double low, double open, TimeInterval timeInterval) {
        this.close = close;
        this.high = high;
        this.low = low;
        this.open = open;
        this.timeInterval = timeInterval;
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
}
