package bitcoinGWT.server.dao.entities;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradeType;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/12/14
 * Time: 1:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesFullLayoutRecord {
    public static final String TRADES_TABLE_SUFFIX = "_full_trades";

    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_AMOUNT = "amount";
    public static final String COLUMN_CURRENCY = "currency";
    public static final String COLUMN_TRADE_ITEM = "trade_item";
    public static final String COLUMN_TRADE_ID = "trade_id";
    public static final String COLUMN_TRADE_TYPE = "trade_type";

    private Currency currency;
    private Currency tradeItem;
    private TradeType type;
    protected Long timestamp;
    protected Double price;
    protected Double amount;
    protected Long tradeId;

    public TradesFullLayoutRecord(Long tradeId, Long timestamp, Double price,
                                  Double amount, Currency currency,
                                  Currency tradeItem,
                                  TradeType type) {
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
        this.price = price;
        this.tradeId = tradeId;
        this.tradeItem = tradeItem;
        this.type = type;
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

    public Long getTimestamp() {
        return timestamp;
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TradesFullLayoutRecord)) return false;

        TradesFullLayoutRecord that = (TradesFullLayoutRecord) o;

        if (tradeId != null ? !tradeId.equals(that.tradeId) : that.tradeId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return tradeId != null ? tradeId.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "TradesFullLayoutRecord{" +
                "amount=" + amount +
                ", currency=" + currency +
                ", tradeItem=" + tradeItem +
                ", type=" + type +
                ", date=" + new Date(timestamp) +
                ", price=" + price +
                ", tradeId=" + tradeId +
                '}';
    }
}
