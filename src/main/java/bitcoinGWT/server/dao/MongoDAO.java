package bitcoinGWT.server.dao;

import bitcoinGWT.server.dao.entities.ChartRecord;
import bitcoinGWT.server.dao.entities.TradesFullLayoutRecord;
import bitcoinGWT.server.dao.entities.TradesHistoryRecord;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradeType;
import com.google.web.bindery.requestfactory.server.Pair;
import com.mongodb.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.FileReader;
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

    private static final Logger LOG = Logger.getLogger(MongoDAO.class);

    //private static String MONGO_PROPERTIES_FILE = "WEB-INF/classes/mongo.properties";
    private static String MONGO_PROPERTIES_FILE = "mongo.properties";

    private Properties connectionProperties;
    private MongoClient mongoClient;

    public MongoDAO() {
        connectionProperties = new Properties();
        try {
            connectionProperties.load(new FileReader(new ClassPathResource(MONGO_PROPERTIES_FILE).getFile()));
        } catch (Exception e) {
            LOG.error("An error has occurred while reading the DB connection properties", e);
        }

        try {
            // Since 2.10.0, uses MongoClient
            mongoClient = new MongoClient(connectionProperties.getProperty(MongoConnectionPropertyKeys.ADDRESS.getKey())
                    , Integer.valueOf(connectionProperties.getProperty(MongoConnectionPropertyKeys.PORT.getKey())));
        } catch (Exception e) {
            LOG.error("An error has occurred while initializing the DB connection", e);
        }
    }

    public void saveTradesHistoryRecords(Pair<String, List<TradesHistoryRecord>> csvRecords, boolean saveLastRecord) {
        DB db = getDBConnection();
        if (db == null) {
            return;
        }

        DBCollection tradesTable = db.getCollection(csvRecords.getA() + TradesHistoryRecord.TRADES_TABLE_SUFFIX);
        //ensure the trades table has the needed indexes
        //changeHistoryTradesTableDefinition(tradesTable);

        Date before = new Date();

        //in case we don't want to save the last record, we can choose not to (this is useful in case the last record
        //will in fact be the next record to be saved, but in a different records batch
        int recordsSizeToSave = saveLastRecord ? csvRecords.getB().size() : csvRecords.getB().size() - 1;

        LOG.info("Start converting " + csvRecords.getB().size() + " history records");

        List<DBObject> dbRecords = new ArrayList<>();
        for (int i = 0; i < recordsSizeToSave; i++) {
            TradesHistoryRecord csvRecord = csvRecords.getB().get(i);
            BasicDBObject document = new BasicDBObject();
            document.put(TradesHistoryRecord.COLUMN_TIME, csvRecord.getTime());
            document.put(TradesHistoryRecord.COLUMN_PRICE, csvRecord.getPrice());
            document.put(TradesHistoryRecord.COLUMN_AMOUNT, csvRecord.getAmount());
            dbRecords.add(document);
        }
        LOG.info("Done converting history records, operation took:" + +(new Date().getTime() - before.getTime()) + " ms");

        before = new Date();
        LOG.info("Start saving " + recordsSizeToSave + " history records");
        try {
            tradesTable.insert(dbRecords);
            LOG.info("Done saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
        } catch (Exception e) {
            LOG.error("Error occurred while saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms", e);
        }
    }

    public Collection<TradesFullLayoutRecord> saveTradesFullLayoutRecords(Pair<String, List<TradesFullLayoutRecord>> csvRecords, boolean saveLastRecord) {
        DB db = getDBConnection();
        if (db == null) {
            return new ArrayList<>();
        }

        DBCollection tradesTable = db.getCollection(csvRecords.getA() + TradesFullLayoutRecord.TRADES_TABLE_SUFFIX);
        changeFullTradesTableDefinition(tradesTable);

        Date before = new Date();

        //in case we don't want to save the last record, we can choose not to (this is useful in case the last record
        //will in fact be the next record to be saved, but in a different records batch
        int recordsSizeToSave = saveLastRecord ? csvRecords.getB().size() : csvRecords.getB().size() - 1;

        LOG.info("Start converting " + csvRecords.getB().size() + " full layout records");

        Map<TradesFullLayoutRecord, DBObject> dbRecords = new LinkedHashMap<>();
        for (int i = 0; i < recordsSizeToSave; i++) {
            TradesFullLayoutRecord fullRecord = csvRecords.getB().get(i);
            if (fullRecord.getAmount() < 0.0001) {
                continue; //don't save any record with a transaction amount less than 0.01 BTC
            }
            BasicDBObject document = new BasicDBObject();
            document.put(TradesFullLayoutRecord.COLUMN_DATE, fullRecord.getTimestamp());
            document.put(TradesFullLayoutRecord.COLUMN_PRICE, fullRecord.getPrice());
            document.put(TradesFullLayoutRecord.COLUMN_AMOUNT, fullRecord.getAmount());
            document.put(TradesFullLayoutRecord.COLUMN_CURRENCY, fullRecord.getCurrency().name());
            document.put(TradesFullLayoutRecord.COLUMN_TRADE_ITEM, fullRecord.getTradeItem().name());
            document.put(TradesFullLayoutRecord.COLUMN_TRADE_ID, fullRecord.getTradeId());
            document.put(TradesFullLayoutRecord.COLUMN_TRADE_TYPE, fullRecord.getType().name());

            dbRecords.put(fullRecord, document);
        }
        LOG.info("Done converting full layout records, operation took:" + +(new Date().getTime() - before.getTime()) + " ms");

        LOG.debug("Going to save: " + dbRecords.toString());

        before = new Date();
        LOG.info("Start saving " + recordsSizeToSave + " CSV records");
        try {
            Iterator<Map.Entry<TradesFullLayoutRecord, DBObject>> iterator = dbRecords.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<TradesFullLayoutRecord, DBObject> record = iterator.next();
                try {
                    tradesTable.insert(record.getValue());
                } catch (MongoException.DuplicateKey dup) {
                    LOG.warn("Trying to insert duplicate record, skipping...(" + record.getValue() + ")", dup);
                    //remove the duplicate records
                    iterator.remove();
                }
            }
            LOG.info("Done saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
        } catch (Exception e) {
            LOG.error("Error occurred while saving full layout records, operation took: " + (new Date().getTime() - before.getTime()) + " ms", e);
        } finally {
            //return the full trades which were not marked as duplicate
            return dbRecords.keySet();
        }
    }

    @Override
    public void saveChartRecords(Pair<String, List<ChartRecord>> chartRecords) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private DB getDBConnection() {
        DB db = mongoClient.getDB(connectionProperties.getProperty(MongoConnectionPropertyKeys.SCHEMA.getKey()));

        String username = connectionProperties.getProperty(MongoConnectionPropertyKeys.USERNAME.getKey());
        String password = connectionProperties.getProperty(MongoConnectionPropertyKeys.PASSWORD.getKey());
        boolean authenticated = false;
        try {
            if (username != null && !username.isEmpty()
                    && password != null & !password.isEmpty()) {
                //need to authenticate
                LOG.info("logging in with credentials" + connectionProperties);
                authenticated = db.authenticate(username, password.toCharArray());
            }
        } catch (Exception e) {
            if (!authenticated) {
                LOG.error(new Exception("Not authenticated to the database"), e);
                return null;
            }
        }

        return db;
    }

    @Override
    public List<TradesHistoryRecord> getTradesHistoryRecords(String marketIdentifier, Long start, Long end, boolean loadLastRecord) {
        DB db = getDBConnection();
        if (db == null) {
            return new ArrayList<>();
        }

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
        DB db = getDBConnection();
        if (db == null) {
            return new ArrayList<>();
        }

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

    @Override
    public void shutdown() {
        LOG.info("Closing DB connection");
        mongoClient.close();
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
                LOG.info("Cannot parse JSON for record:" + obj);
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
                LOG.info("Cannot parse JSON for record:" + obj);
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
