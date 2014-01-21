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
            //ensure the trades table has the needed indexes
            //changeHistoryTradesTableDefinition(tradesTable);

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
            System.out.println("Done converting CSV records, operation took:" + +(new Date().getTime() - before.getTime()) + " ms");

            before = new Date();
            System.out.println("Start saving " + recordsSizeToSave + " CSV records");
            try {
                //for (DBObject document : dbRecords) {
                    //try {
                        tradesTable.insert(dbRecords);
                    //} catch (MongoException.DuplicateKey dup) {
                        //System.err.println("Trying to insert duplicate record, skipping...(" + document + ")");
                    //}
                //}
                System.out.println("Done saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error occurred while saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
            }
        }
    }

    public void saveTradesFullLayoutRecords(Map<String, List<TradesFullLayoutRecord>> csvRecords, boolean saveLastRecord) {
        DB db = mongoClient.getDB(connectionProperties.getProperty(MongoConnectionPropertyKeys.SCHEMA.getKey()));

        for (Map.Entry<String, List<TradesFullLayoutRecord>> entry : csvRecords.entrySet()) {
            DBCollection tradesTable = db.getCollection(entry.getKey() + TradesFullLayoutRecord.TRADES_TABLE_SUFFIX);
            changeFullTradesTableDefinition(tradesTable);

            Date before = new Date();

            //in case we don't want to save the last record, we can choose not to (this is useful in case the last record
            //will in fact be the next record to be saved, but in a different records batch
            int recordsSizeToSave = saveLastRecord ? entry.getValue().size() : entry.getValue().size() - 1;

            System.out.println("Start converting " + entry.getValue().size() + " full layout records");

            List<DBObject> dbRecords = new ArrayList<>();
            for (int i = 0; i < recordsSizeToSave; i++) {
                TradesFullLayoutRecord fullRecord = entry.getValue().get(i);
                if (fullRecord.getAmount() < 0.0001) {
                    return; //don't save any record with a transaction amount less than 0.01 BTC
                }
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
            System.out.println("Done converting full layout records, operation took:" + +(new Date().getTime() - before.getTime()) + " ms");

            before = new Date();
            System.out.println("Start saving " + recordsSizeToSave + " CSV records");
            try {
                for (DBObject document : dbRecords) {
                    try {
                        tradesTable.insert(document);
                    } catch (MongoException.DuplicateKey dup) {
                        System.err.println("Trying to insert duplicate record, skipping...(" + document + ")");
                    }
                }
                System.out.println("Done saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Error occurred while saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
            }
        }
    }

    @Override
    public List<TradesHistoryRecord> getTradesHistoryRecords(String marketIdentifier, Long start, Long end, boolean loadLastRecord) {
        DB db = mongoClient.getDB(connectionProperties.getProperty(MongoConnectionPropertyKeys.SCHEMA.getKey()));
        DBCollection tradesTable = db.getCollection(marketIdentifier + TradesHistoryRecord.TRADES_TABLE_SUFFIX);

        List<TradesHistoryRecord> result = new ArrayList<>();

        BasicDBObject sortQuery = new BasicDBObject();
        //sort descending
        sortQuery.put(TradesHistoryRecord.COLUMN_TIME, -1);
        if (start == null && end == null && loadLastRecord && tradesTable.count() > 0) {    //have to return the last records of the same timestamp
            //try to identify if there are any more records at the same timestamp
            BasicDBObject objectsAtTimestampQuery = new BasicDBObject();
            objectsAtTimestampQuery.put(TradesHistoryRecord.COLUMN_TIME,
                    tradesTable.find().sort(sortQuery).limit(1).next().get(TradesHistoryRecord.COLUMN_TIME));
            //load just one record, in order to see the latest timestamp
            DBCursor cursor = tradesTable.find(objectsAtTimestampQuery).sort(sortQuery);

            //take just one element and add it to the partial results
            parseHistoryTradesDBRecords(result, cursor);

           /* //we should have only one record here:
            if (partialResults.size() == 1) {
                //try to identify if there are any more records at the same timestamp
                BasicDBObject objectsAtTimestampQuery = BasicDBObject();
                objectsAtTimestampQuery.put(TradesHistoryRecord.COLUMN_TIME, new BasicDBObject(""))
                DBCursor cursor = tradesTable.find().sort(sortQuery).limit(1);
            }*/
        } else {    //have to return all the records since "timestamp"
            BasicDBObject gteQuery = new BasicDBObject();
            gteQuery.put(TradesHistoryRecord.COLUMN_TIME, new BasicDBObject("$gte", start)); //all records with date greater than or equal with timestamp
            if (end != null) {  //this means we have a time interval specified
                gteQuery.put(TradesHistoryRecord.COLUMN_TIME, new BasicDBObject("$lte", end)); //all records with date greater than or equal with timestamp
            }
            DBCursor cursor = tradesTable.find(gteQuery).sort(sortQuery);
            parseHistoryTradesDBRecords(result, cursor);
        }

        return result;
    }

    @Override
    public List<TradesFullLayoutRecord> getTradesFullLayoutRecords(String marketIdentifier, Long timestamp, boolean loadLastRecord) {
        DB db = mongoClient.getDB(connectionProperties.getProperty(MongoConnectionPropertyKeys.SCHEMA.getKey()));
        DBCollection tradesTable = db.getCollection(marketIdentifier + TradesFullLayoutRecord.TRADES_TABLE_SUFFIX);

        List<TradesFullLayoutRecord> result = new ArrayList<>();

        BasicDBObject sortQuery = new BasicDBObject();
        //sort descending
        sortQuery.put(TradesFullLayoutRecord.COLUMN_DATE, -1);
        if (timestamp == null) {    //have to return the last TRADE_SIZE records
            int tradesSize = loadLastRecord ? 1 : TRADES_SIZE;  //if load just the last record, then load just one record
            DBCursor cursor = tradesTable.find().sort(sortQuery).limit(tradesSize);
            parseFullLayoutTradesDBRecords(result, cursor);
        } else {    //have to return all the records since "timestamp"
            BasicDBObject gteQuery = new BasicDBObject();
            gteQuery.put(TradesFullLayoutRecord.COLUMN_DATE, new BasicDBObject("$gte", timestamp)); //all records with date greater than or equal with timestamp
            DBCursor cursor = tradesTable.find(gteQuery).sort(sortQuery);
            parseFullLayoutTradesDBRecords(result, cursor);
        }

        return result;
    }

    @Override
    public List<TradesFullLayoutRecord> getLatestFullLayoutRecords(String marketIdentifier) {
        return getTradesFullLayoutRecords(marketIdentifier, null, true);
    }

    @Override
    public List<TradesHistoryRecord> getLatestHistoryTrades(String marketIdentifier) {
        return getTradesHistoryRecords(marketIdentifier, null, null, true);
    }

    private void parseFullLayoutTradesDBRecords(List<TradesFullLayoutRecord> result, DBCursor cursor) {
        while (cursor.hasNext()) {
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

    private void changeFullTradesTableDefinition(DBCollection tradesTable) {
        BasicDBObject indexProperties = new BasicDBObject();
        indexProperties.put("unique", true);
        indexProperties.put("dropDups", true);
        tradesTable.ensureIndex(new BasicDBObject(TradesFullLayoutRecord.COLUMN_TRADE_ID, 1), indexProperties);
    }

    private void parseHistoryTradesDBRecords(List<TradesHistoryRecord> result, DBCursor cursor) {
        while (cursor.hasNext()) {
            DBObject obj = cursor.next();
            try {
                Long date = (Long) obj.get(TradesHistoryRecord.COLUMN_TIME);
                Double price = (Double) obj.get(TradesHistoryRecord.COLUMN_PRICE);
                Double amount = (Double) obj.get(TradesHistoryRecord.COLUMN_AMOUNT);
                TradesHistoryRecord record = new TradesHistoryRecord(date, amount, price);
                result.add(record);
            } catch (Exception e) {
                System.out.println("Cannot parse JSON for record:" + obj);
            }
        }
    }

    private void changeHistoryTradesTableDefinition(DBCollection historyTable) {
        BasicDBObject indexProperties = new BasicDBObject();
        indexProperties.put("unique", true);
        indexProperties.put("dropDups", true);

        BasicDBObject indexColumns = new BasicDBObject();
        indexColumns.put(TradesHistoryRecord.COLUMN_TIME, 1);
        indexColumns.put(TradesHistoryRecord.COLUMN_AMOUNT, 1);
        indexColumns.put(TradesHistoryRecord.COLUMN_PRICE, 1);
        historyTable.ensureIndex(indexColumns, indexProperties);
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
