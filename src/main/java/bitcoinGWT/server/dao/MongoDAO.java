package bitcoinGWT.server.dao;

import bitcoinGWT.server.dao.entities.TradesFullLayoutRecord;
import bitcoinGWT.server.dao.entities.TradesHistoryRecord;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradeType;
import com.mongodb.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.net.UnknownHostException;
import java.util.*;

import static bitcoinGWT.shared.model.Constants.*;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/11/14
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Qualifier("MONGO")
public class MongoDAO implements GenericDAO {

    private static String MONGO_PROPERTIES_FILE = "mongo.properties";

    private Properties connectionProperties;
    private MongoClient mongoClient;

    public MongoDAO() {
        connectionProperties = new Properties();
        try {
            connectionProperties.load(new FileReader(new ClassPathResource(MONGO_PROPERTIES_FILE).getFile()));
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            // Since 2.10.0, uses MongoClient
            mongoClient = new MongoClient(connectionProperties.getProperty(MongoConnectionPropertyKeys.ADDRESS.getKey())
                    , Integer.valueOf(connectionProperties.getProperty(MongoConnectionPropertyKeys.PORT.getKey())));
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void saveTradesHistoryRecords(Map<String, List<TradesHistoryRecord>> csvRecords, boolean saveLastRecord) {
        DB db = mongoClient.getDB(connectionProperties.getProperty(MongoConnectionPropertyKeys.SCHEMA.getKey()));

        for (Map.Entry<String, List<TradesHistoryRecord>> entry : csvRecords.entrySet()) {
            DBCollection tradesTable = db.getCollection(entry.getKey() + TradesHistoryRecord.TRADES_TABLE_SUFFIX);

            Date before = new Date();

            //in case we don't want to save the last record, we can choose not to (this is useful in case the last record
            //will in fact be the next record to be saved, but in a different records batch
            int recordsSizeToSave = saveLastRecord ? entry.getValue().size() : entry.getValue().size() - 1;

            System.out.println("Start converting " + entry.getValue().size() + " CSV records");

            List<DBObject> dbRecords = new ArrayList<>();
            for (int i = 0; i < recordsSizeToSave; i++) {
                TradesHistoryRecord csvRecord = entry.getValue().get(i);
                BasicDBObject document = new BasicDBObject();
                document.put(TradesHistoryRecord.COLUMN_TIME, csvRecord.getTime());
                document.put(TradesHistoryRecord.COLUMN_PRICE, csvRecord.getPrice());
                document.put(TradesHistoryRecord.COLUMN_AMOUNT, csvRecord.getAmount());
                dbRecords.add(document);
            }
            System.out.println("Done converting CSV records, operation took:" + + (new Date().getTime() - before.getTime()) + " ms");

            before = new Date();
            System.out.println("Start saving " + recordsSizeToSave + " CSV records");
            try {
                tradesTable.insert(dbRecords);
                System.out.println("Done saving CSV records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error occurred while saving CSV records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
            }
        }
    }

    public void saveTradesFullLayoutRecords(Map<String, List<TradesFullLayoutRecord>> csvRecords, boolean saveLastRecord) {
        DB db = mongoClient.getDB(connectionProperties.getProperty(MongoConnectionPropertyKeys.SCHEMA.getKey()));

        for (Map.Entry<String, List<TradesFullLayoutRecord>> entry : csvRecords.entrySet()) {
            DBCollection tradesTable = db.getCollection(entry.getKey() + TradesFullLayoutRecord.TRADES_TABLE_SUFFIX);

            Date before = new Date();

            //in case we don't want to save the last record, we can choose not to (this is useful in case the last record
            //will in fact be the next record to be saved, but in a different records batch
            int recordsSizeToSave = saveLastRecord ? entry.getValue().size() : entry.getValue().size() - 1;

            System.out.println("Start converting " + entry.getValue().size() + " full layout records");

            List<DBObject> dbRecords = new ArrayList<>();
            for (int i = 0; i < recordsSizeToSave; i++) {
                TradesFullLayoutRecord fullRecord = entry.getValue().get(i);
                BasicDBObject document = new BasicDBObject();
                document.put(TradesFullLayoutRecord.COLUMN_DATE, fullRecord.getTimestamp());
                document.put(TradesFullLayoutRecord.COLUMN_PRICE, fullRecord.getPrice());
                document.put(TradesFullLayoutRecord.COLUMN_AMOUNT, fullRecord.getAmount());
                document.put(TradesFullLayoutRecord.COLUMN_CURRENCY, fullRecord.getCurrency().name());
                document.put(TradesFullLayoutRecord.COLUMN_TRADE_ITEM, fullRecord.getTradeItem().name());
                document.put(TradesFullLayoutRecord.COLUMN_TRADE_ID, fullRecord.getTradeId());
                document.put(TradesFullLayoutRecord.COLUMN_TRADE_TYPE, fullRecord.getType().name());

                dbRecords.add(document);
            }
            System.out.println("Done converting full layout records, operation took:" + + (new Date().getTime() - before.getTime()) + " ms");

            before = new Date();
            System.out.println("Start saving " + recordsSizeToSave + " CSV records");
            try {
                tradesTable.insert(dbRecords);
                System.out.println("Done saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error occurred while saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
            }
        }
    }

    @Override
    public List<TradesHistoryRecord> getTradesHistoryRecords(String marketIdentifier, Long timestamp, boolean loadLastRecord) {
        return null;
    }

    @Override
    public List<TradesFullLayoutRecord> getTradesFullLayoutRecords(String marketIdentifier, Long timestamp, boolean loadLastRecord) {
        //db.foo.find().sort({_id:1}).limit(50);
        DB db = mongoClient.getDB(connectionProperties.getProperty(MongoConnectionPropertyKeys.SCHEMA.getKey()));
        DBCollection tradesTable = db.getCollection(marketIdentifier + TradesFullLayoutRecord.TRADES_TABLE_SUFFIX);

        List<TradesFullLayoutRecord> result = new ArrayList<>();

        BasicDBObject sortQuery = new BasicDBObject();
        //sort descending
        sortQuery.put(TradesFullLayoutRecord.COLUMN_DATE, -1);
        if (timestamp == null) {    //have to return the last TRADE_SIZE records
            DBCursor cursor = tradesTable.find().sort(sortQuery).limit(TRADES_SIZE);
            parseDBRecords(result, cursor);
        } else {    //have to return all the records since "timestamp"
            BasicDBObject gteQuery = new BasicDBObject();
            gteQuery.put(TradesFullLayoutRecord.COLUMN_DATE, new BasicDBObject("$gte", timestamp)); //all records with date greater than or equal with timestamp
            DBCursor cursor = tradesTable.find(gteQuery).sort(sortQuery);
            parseDBRecords(result, cursor);
        }



        return result;
    }

    private void parseDBRecords(List<TradesFullLayoutRecord> result, DBCursor cursor) {
        while(cursor.hasNext()) {
            DBObject obj = cursor.next();
            try {
                Long date = (Long) obj.get(TradesFullLayoutRecord.COLUMN_DATE);
                Double price = (Double) obj.get(TradesFullLayoutRecord.COLUMN_PRICE);
                Double amount = (Double) obj.get(TradesFullLayoutRecord.COLUMN_AMOUNT);
                Currency currency = Currency.valueOf(String.valueOf(obj.get(TradesFullLayoutRecord.COLUMN_CURRENCY)));
                Currency tradeItem = Currency.valueOf(String.valueOf(obj.get(TradesFullLayoutRecord.COLUMN_TRADE_ITEM)));
                Long tradeId = (Long) obj.get(TradesFullLayoutRecord.COLUMN_TRADE_ID);
                TradeType type = TradeType.valueOf(String.valueOf(obj.get(TradesFullLayoutRecord.COLUMN_TRADE_TYPE)));
                TradesFullLayoutRecord record = new TradesFullLayoutRecord(tradeId, date, price, amount, currency, tradeItem, type);
                result.add(record);
            } catch (Exception e) {
                System.out.println("Cannot parse JSON for record:" + obj);
            }
        }
    }

    private enum MongoConnectionPropertyKeys {
        ADDRESS("db.address"), PORT("db.port"), SCHEMA("db.schema"), USERNAME("db.username"), PASSWORD("db.password");
        private String key;

        MongoConnectionPropertyKeys(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
    }
}
