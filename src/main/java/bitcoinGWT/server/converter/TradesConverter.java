package bitcoinGWT.server.converter;

import bitcoinGWT.server.dao.entities.TradesFullLayoutRecord;
import bitcoinGWT.server.dao.entities.TradesHistoryRecord;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import bitcoinGWT.shared.model.TradesShallowObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/12/14
 * Time: 1:25 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesConverter {

    public static List<TradesFullLayoutObject> convertTradesFullLayoutRecordsToTradesFullLayoutObjects(
            List<TradesFullLayoutRecord> tradeRecords) {

        List<TradesFullLayoutObject> result = new ArrayList<>();
        for (TradesFullLayoutRecord record : tradeRecords) {
            TradesFullLayoutObject trade = new TradesFullLayoutObject(record.getTradeId(), new Date(record.getTimestamp()),
                    record.getPrice(), record.getAmount(), record.getCurrency(), record.getTradeItem(),
                    record.getType());
            result.add(trade);
        }

        return result;
    }

    public static List<TradesFullLayoutRecord> convertTradesFullLayoutObjectsToTradesFullLayoutRecords(
            List<TradesFullLayoutObject> tradeObjects) {

        List<TradesFullLayoutRecord> result = new ArrayList<>();
        for (TradesFullLayoutObject trade : tradeObjects) {
            TradesFullLayoutRecord record = new TradesFullLayoutRecord(trade.getTradeId(), trade.getDate().getTime(),
                    trade.getPrice(), trade.getAmount(), trade.getCurrency(), trade.getTradeItem(),
                    trade.getType());
            result.add(record);
        }

        return result;
    }

    public static List<TradesShallowObject> convertTradesHistoryRecordsToTradesShallowObjects(
            List<TradesHistoryRecord> historyRecords) {
        List<TradesShallowObject> result = new ArrayList<>();
        for (TradesHistoryRecord record : historyRecords) {
            TradesShallowObject trade = new TradesShallowObject(new Date(record.getTime()), record.getAmount(), record.getPrice());
            result.add(trade);
        }

        return result;
    }

    public static List<TradesHistoryRecord> convertTradesShallowObjectsToTradesHistoryRecords(
            List<? extends TradesShallowObject> shallowObjects) {
        List<TradesHistoryRecord> result = new ArrayList<>();
        for (TradesShallowObject trade : shallowObjects) {
            TradesHistoryRecord record = new TradesHistoryRecord(trade.getDate().getTime(), trade.getAmount(), trade.getPrice());
            result.add(record);
        }

        return result;
    }
}
