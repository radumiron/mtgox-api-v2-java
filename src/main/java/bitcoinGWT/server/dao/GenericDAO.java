package bitcoinGWT.server.dao;

import bitcoinGWT.server.dao.entities.TradesFullLayoutRecord;
import bitcoinGWT.server.dao.entities.TradesHistoryRecord;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/12/14
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public interface GenericDAO {

    public void saveTradesHistoryRecords(Map<String, List<TradesHistoryRecord>> csvRecords, boolean saveLastRecord);
    public void saveTradesFullLayoutRecords(Map<String, List<TradesFullLayoutRecord>> fullLayoutRecords, boolean saveLastRecord);

    List<TradesHistoryRecord> getTradesHistoryRecords(String marketIdentifier, Long start, Long end, boolean loadLastRecord);
    public List<TradesFullLayoutRecord> getTradesFullLayoutRecords(String marketIdentifier, Long timestamp, boolean loadLastRecord);

    public List<TradesFullLayoutRecord> getLatestFullLayoutRecords(String marketIdentifier);
    public List<TradesHistoryRecord> getLatestHistoryTrades(String marketIdentifier);


}
