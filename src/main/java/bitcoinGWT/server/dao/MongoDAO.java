package bitcoinGWT.server.dao;

import bitcoinGWT.server.dao.entities.TradesCSVRecord;
import com.mongodb.*;
import history.HistoryDownloader;
import org.springframework.core.io.ClassPathResource;

import java.io.FileReader;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

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

    public void saveCSVRecords(List<TradesCSVRecord> csvRecords, boolean saveLastRecord) {
        DB db = mongoClient.getDB(connectionProperties.getProperty(MongoConnectionPropertyKeys.SCHEMA.getKey()));
        DBCollection tradesTable = db.getCollection(TradesCSVRecord.TABLE_NAME);

        Date before = new Date();
        System.out.println("Start converting " + csvRecords.size() + " CSV records");

        //in case we don't want to save the last record, we can choose not to (this is useful in case the last record
        //will in fact be the next record to be saved, but in a different records batch
        int recordsSizeToSave = saveLastRecord ? csvRecords.size() : csvRecords.size() - 1;

        List<DBObject> dbRecords = new ArrayList<>();
        for (int i = 0; i < recordsSizeToSave; i++) {
            TradesCSVRecord csvRecord = csvRecords.get(i);
            BasicDBObject document = new BasicDBObject();
            document.put(TradesCSVRecord.COLUMN_TIME, csvRecord.getTime());
            document.put(TradesCSVRecord.COLUMN_PRICE, csvRecord.getPrice());
            document.put(TradesCSVRecord.COLUMN_AMOUNT, csvRecord.getAmount());
            dbRecords.add(document);
        }
        System.out.println("Done converting CSV records, operation took:" + + (new Date().getTime() - before.getTime()) + " ms");

        before = new Date();
        System.out.println("Start saving " + csvRecords.size() + " CSV records");
        try {
            tradesTable.insert(dbRecords);
            System.out.println("Done saving CSV records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error occurred while saving CSV records, operation took: " + (new Date().getTime() - before.getTime()) + " ms");
        }

    }
}
