package bitcoinGWT.server.dao.entities;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/11/14
 * Time: 11:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesRecord {

    public static final String TRADES_TABLE_SUFFIX = "_trades";

    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_AMOUNT = "amount";

    private long time;
    private double price;
    private double amount;

    public TradesRecord(long time, double amount, double price) {
        this.amount = amount;
        this.price = price;
        this.time = time;
    }

    public double getAmount() {
        return amount;
    }

    public double getPrice() {
        return price;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "TradesRecord{" +
                "amount=" + amount +
                ", time=" + time +
                ", price=" + price +
                '}';
    }
}
