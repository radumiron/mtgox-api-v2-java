package bitcoinGWT.shared.model;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/22/13
 * Time: 7:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class TickerFullLayoutObject extends TickerShallowObject {

   /* *   "result":"success",
            *   "data": {
        *       "high":       **Currency Object - USD**,
        *       "low":        **Currency Object - USD**,
        *       "avg":        **Currency Object - USD**,
        *       "vwap":       **Currency Object - USD**,
        *       "vol":        **Currency Object - BTC**,
        *       "last_local": **Currency Object - USD**,
        *       "last_orig":  **Currency Object - ???**,
        *       "last_all":   **Currency Object - USD**,
        *       "last":       **Currency Object - USD**,
        *       "buy":        **Currency Object - USD**,
        *       "sell":       **Currency Object - USD**,
        *       "now":        "1364689759572564"*/

    private double high;
    private double low;
    private double average;
    private double vwap;
    private double volume;
    private double lastLocal;
    private double lastOrig;
    private double lastAll;
    private double bid;
    private double ask;

    public TickerFullLayoutObject(Currency currency, double price, Date now, double ask, double average, double bid, double high, double lastAll, double lastLocal, double lastOrig, double low, double volume, double vwap) {
        super(currency, price, now);
        this.ask = ask;
        this.average = average;
        this.bid = bid;
        this.high = high;
        this.lastAll = lastAll;
        this.lastLocal = lastLocal;
        this.lastOrig = lastOrig;
        this.low = low;
        this.volume = volume;
        this.vwap = vwap;
    }

    public TickerFullLayoutObject() {
    }

    public double getAsk() {
        return ask;
    }

    public double getAverage() {
        return average;
    }

    public double getBid() {
        return bid;
    }

    public double getHigh() {
        return high;
    }

    public double getLastAll() {
        return lastAll;
    }

    public double getLastLocal() {
        return lastLocal;
    }

    public double getLastOrig() {
        return lastOrig;
    }

    public double getLow() {
        return low;
    }

    public double getVolume() {
        return volume;
    }

    public double getVwap() {
        return vwap;
    }

    @Override
    public String toString() {
        return "TickerFullLayoutObject{" +
                "ask=" + ask +
                ", high=" + high +
                ", low=" + low +
                ", average=" + average +
                ", vwap=" + vwap +
                ", volume=" + volume +
                ", lastLocal=" + lastLocal +
                ", lastOrig=" + lastOrig +
                ", lastAll=" + lastAll +
                ", bid=" + bid +
                "} " + super.toString();
    }
}
