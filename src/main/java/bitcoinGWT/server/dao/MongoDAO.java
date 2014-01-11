package bitcoinGWT.server.dao;

import bitcoinGWT.server.dao.entities.TradesRecord;
import com.mongodb.*;
import org.springframework.core.io.ClassPathResource;

import java.io.FileReader;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/11/14
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class MongoDAO {

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

    public void saveTradesRecords(Map<String, List<TradesRecord>> csvRecords, boolean saveLastRecord) {
        DB db = mongoClient.getDB(connectionProperties.getProperty(MongoConnectionPropertyKeys.SCHEMA.getKey()));

        for (Map.Entry<String, List<TradesRecord>> entry : csvRecords.entrySet()) {
            DBCollection tradesTable = db.getCollection(entry.getKey() + TradesRecord.TRADES_TABLE_SUFFIX);

            Date before = new Date();

            //in case we don't want to save the last record, we can choose not to (this is useful in case the last record
            //will in fact be the next record to be saved, but in a different records batch
            int recordsSizeToSave = saveLastRecord ? entry.getValue().size() : entry.getValue().size() - 1;

            System.out.println("Start converting " + entry.getValue().size() + " CSV records");

            List<DBObject> dbRecords = new ArrayList<>();
            for (int i = 0; i < recordsSizeToSave; i++) {
                TradesRecord csvRecord = entry.getValue().get(i);
                BasicDBObject document = new BasicDBObject();
                document.put(TradesRecord.COLUMN_TIME, csvRecord.getTime());
                document.put(TradesRecord.COLUMN_PRICE, csvRecord.getPrice());
                document.put(TradesRecord.COLUMN_AMOUNT, csvRecord.getAmount());
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
}
