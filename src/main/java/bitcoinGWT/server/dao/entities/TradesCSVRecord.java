package bitcoinGWT.server.dao.entities;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/11/14
 * Time: 11:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesCSVRecord {

    public static final String TABLE_NAME = "trades";

    public static final String COLUMN_TIME = "time";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_AMOUNT = "amount";

    private long time;
    private double price;
    private double amount;

    public TradesCSVRecord(long time, double amount, double price) {
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
        return "TradesCSVRecord{" +
                "amount=" + amount +
                ", time=" + time +
                ", price=" + price +
                '}';
    }
}
