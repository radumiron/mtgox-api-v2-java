package bitcoinGWT.server.history;

import bitcoinGWT.server.converter.TradesConverter;
import bitcoinGWT.server.dao.GenericDAO;
import bitcoinGWT.server.dao.MongoDAO;
import bitcoinGWT.server.dao.entities.TradesFullLayoutRecord;
import bitcoinGWT.server.dao.entities.TradesHistoryRecord;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import bitcoinGWT.shared.model.TimeInterval;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.google.common.io.Resources;
import com.google.gwt.rpc.server.Pair;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import trading.api_interfaces.TradeInterface;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static bitcoinGWT.shared.model.Constants.INITIAL_TRADES_INTERVAL;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/11/14
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class HistoryDownloader {

    private static final Logger LOG = Logger.getLogger(HistoryDownloader.class);

    private static final String ENCODING = "UTF-8";
    private static final String CSV_SPLITTER = ",";

    private boolean printHttpResponse = true;

    private ConcurrentHashMap<MultiKey, Long> previousTimestampMap;

    @Autowired
    @Qualifier("XChange")
    private TradeInterface trade;

    @Autowired
    @Qualifier("MONGO")
    private GenericDAO dao;

    @PostConstruct
    private void init() {
        LOG.info("init History downloader");
        previousTimestampMap = new ConcurrentHashMap<>();
        try {
            performTradesImport();
        } catch (Exception e) {
            LOG.error("Error occurred while initializing the History Downloader", e);
        }
    }

    public static void main(String[] args) {
        new HistoryDownloader().performTradesImport();
    }

    private void performTradesImport() {
        Markets demoMarket = Markets.MTGOX;
        Currency demoCurrency = Currency.EUR;

        LOG.info("Start initializing trades & history for market:" + getMarketIdentifierName(demoMarket, demoCurrency));

        long bigTimestamp = new Date().getTime();

        //#POINT1
        long timestamp = new Date().getTime();
        LOG.info("Parsing CSV records");
        TradesHistoryRecord latestCSVRecord = processCSVRecords(demoMarket, demoCurrency);
        LOG.info("Done parsing CSV records, operation took:" + (new Date().getTime() - timestamp) + " ms");
        LOG.info("Latest CSV record is:" + latestCSVRecord);

        timestamp = new Date().getTime();
        LOG.info("Parsing API CSV records");
        latestCSVRecord = processAPICSVRecords(demoMarket, demoCurrency, latestCSVRecord);
        LOG.info("Done parsing CSV records, operation took:" + (new Date().getTime() - timestamp) + " ms");
        LOG.info("Latest API CSV record is:" + latestCSVRecord);

        timestamp = new Date().getTime();
        LOG.info("Getting full layout trades");
        List<TradesFullLayoutObject> trades = getFullLayoutTrades(demoMarket, demoCurrency);
        LOG.info("Done getting full layout trades (size = " + trades.size() + "), operation took:" + (new Date().getTime() - timestamp) + " ms");

        timestamp = new Date().getTime();
        LOG.info("Saving full layout trades into the DB");
        Collection<TradesFullLayoutRecord> actuallySavedTrades = saveFullLayoutTrades(demoMarket, demoCurrency, trades);
        LOG.info("Done saving full layout trades into the DB, operation took:" + (new Date().getTime() - timestamp) + " ms");

        timestamp = new Date().getTime();

        if (actuallySavedTrades.size() > 0) {
            LOG.info("Saving history trades into the DB");
            saveHistoryTrades(demoMarket, demoCurrency, latestCSVRecord, actuallySavedTrades);
            LOG.info("Done saving history trades into the DB, operation took:" + (new Date().getTime() - timestamp) + " ms");
        } else {
            LOG.info("There are no records to be saved to the history DB");
        }

        LOG.info("Finished initializing trades & history for market: " + getMarketIdentifierName(demoMarket, demoCurrency)
                + ", operation took:" + (new Date().getTime() - bigTimestamp) + " ms");
    }

    private void saveHistoryTrades(Markets demoMarket, Currency demoCurrency, TradesHistoryRecord latestCSVRecord, Collection<TradesFullLayoutRecord> trades) {
        LOG.info("Have to save " + trades.size() + " history records");
        //#POINT5
        //save also data for the history (for the chart), but only the ones between POINT2 & current time
        List<TradesHistoryRecord> filteredRecords = filterOutOldHistoryTrades(latestCSVRecord, TradesConverter.convertTradesFullLayoutRecordsToTradesHistoryRecords(trades), true);
        LOG.info("After filtering the records, there are " + filteredRecords.size() + " history records which are newer than the latest trade in the history: " + latestCSVRecord);

        Pair<String, List<TradesHistoryRecord>> historyRecordsToSave = new Pair<>(HistoryDownloader.getMarketIdentifierName(demoMarket, demoCurrency),
                filteredRecords);//filter out the trades which are older than the latest history trades loaded from bitcoin-charts (POINT2)
        dao.saveTradesHistoryRecords(historyRecordsToSave, true);
    }

    private Collection<TradesFullLayoutRecord> saveFullLayoutTrades(Markets demoMarket, Currency demoCurrency, List<TradesFullLayoutObject> trades) {
        //#POINT4
        //save the new trades in the DB, both full layout & history
        //save the new trades in the database
        Pair<String, List<TradesFullLayoutRecord>> recordsToSave = new Pair<>(HistoryDownloader.getMarketIdentifierName(demoMarket, demoCurrency),
                TradesConverter.convertTradesFullLayoutObjectsToTradesFullLayoutRecords(trades));
        return dao.saveTradesFullLayoutRecords(recordsToSave, true);
    }

    private List<TradesFullLayoutObject> getFullLayoutTrades(Markets demoMarket, Currency demoCurrency) {
        //#POINT3
        //then, we'll have to use the XChange machine to load the latest data into the MongoDB schema
        List<TradesFullLayoutObject> trades = trade.getTrades(demoMarket, demoCurrency, getFullTradesInitialDownloadTime(getMarketIdentifierName(demoMarket, demoCurrency)));

        //save the last timestamp in the map. This timestamp will be used when downloading the newest trades
        previousTimestampMap.put(new MultiKey(demoMarket, demoCurrency), trades.get(trades.size() - 1).getDate().getTime());
        return trades;
    }

    private TradesHistoryRecord processAPICSVRecords(Markets demoMarket, Currency demoCurrency, TradesHistoryRecord latestCSVRecord) {
        //then, try to load all the trades from bitcoincharts, which happened between the last record in the DB and
        //as close as possible to the current time (the bitcoincharts API returns trades with a latency of ~ 15 minutes)
        //#POINT2
        Calendar calendar = GregorianCalendar.getInstance();
        if (latestCSVRecord == null) {
            //if there are no records in the DB, and we don't have any records from the CSV
            //try to take the trades from the last day
            calendar.add(Calendar.SECOND, (-1) * TimeInterval.ONE_DAY.getSeconds());
        } else {
            //if we do have a History/CSV record, set it to the calendar
            calendar.setTimeInMillis(latestCSVRecord.getTime());
        }
        List<TradesHistoryRecord> apiCSVRecords = executeQuery(demoMarket, demoCurrency, calendar.getTimeInMillis());

        LOG.info("Got " + apiCSVRecords.size() + " history trades from the API");
        Pair<String, List<TradesHistoryRecord>> marketToCSVRecords = new Pair<>(getMarketIdentifierName(demoMarket, demoCurrency)
                , apiCSVRecords);
        //save the API records in the DB
        dao.saveTradesHistoryRecords(marketToCSVRecords, true);

        if (!apiCSVRecords.isEmpty()) {
            latestCSVRecord = apiCSVRecords.get(apiCSVRecords.size() - 1);
        }

        LOG.info("Latest API CSV record, which was saved in the DB:" + latestCSVRecord);
        return latestCSVRecord;
    }

    private TradesHistoryRecord processCSVRecords(Markets demoMarket, Currency demoCurrency) {
        //first, populate the MongoDB schema with historical data read from the CSV file
        List<TradesHistoryRecord> csvRecords = readCSV(demoMarket, demoCurrency);

        LOG.info("There are " + csvRecords.size() + " CSV records to be saved");

        List<TradesHistoryRecord> historyRecords = dao.getLatestHistoryTrades(getMarketIdentifierName(demoMarket, demoCurrency));
        TradesHistoryRecord latestHistoryRecord = null;
        if (!historyRecords.isEmpty()) {
            //all records will have the same timestamp, since they're all the trades from the latest timestamp
            latestHistoryRecord = historyRecords.get(0);
            LOG.info("Already have some data in the DB, latest history trade: " + latestHistoryRecord);

            csvRecords = filterOutOldHistoryTrades(latestHistoryRecord, csvRecords, false);
            LOG.info("After filtering the CSV history trades, still have to save " + csvRecords.size() + " records in the DB");
        }

        Pair<String, List<TradesHistoryRecord>> marketToCSVRecords = new Pair<>(getMarketIdentifierName(demoMarket, demoCurrency)
                , csvRecords);
        dao.saveTradesHistoryRecords(marketToCSVRecords, false);

        //last record got from the CSV, this will be the threshold of getting the trades from the bitcoincharts API
        if (csvRecords.isEmpty() && latestHistoryRecord != null) {
            LOG.info("No records were to be saved in the DB, since we've got all the necessary trades in the history");
            return latestHistoryRecord; //this is the last record from the history DB
        } else if (!csvRecords.isEmpty()) {
            LOG.info("Returning last trade from the CSV, which was not saved in the DB:" + csvRecords.get(csvRecords.size() - 1));
            return csvRecords.get(csvRecords.size() - 1);   //this is the last record from the CSV file
        } else {
            return null;
        }
    }

    private List<TradesHistoryRecord> filterOutOldHistoryTrades(TradesHistoryRecord latestAPICSVRecord, List<TradesHistoryRecord> historyRecords, boolean useMillis) {
        //we dont have an API record to refer to
        if (latestAPICSVRecord == null) {
            return historyRecords;
        }
        ArrayDeque<TradesHistoryRecord> deque = new ArrayDeque<>(historyRecords);
        List<TradesHistoryRecord> result = new ArrayList<>();

        long timeDivider = useMillis ? 1000 : 1;

        TradesHistoryRecord record;
        while ((record = deque.pollLast()).getTime() / timeDivider > latestAPICSVRecord.getTime()) {
            result.add(record);
        }

        Collections.reverse(result);

        return result;
    }

    private Long getFullTradesInitialDownloadTime(String marketIdentifier) {
        Calendar calendar = GregorianCalendar.getInstance();
        //go back the specified amount of seconds
        calendar.add(Calendar.SECOND, (-1) * TimeInterval.ONE_DAY.getSeconds());

        List<TradesFullLayoutRecord> latestDBObjects = dao.getLatestFullLayoutRecords(marketIdentifier);
        //there should be just one record in the list, compare the 1 day time with the latest record and save retrieve only trades which are later than:
        // - 1 DAY
        // - timestamp of latest trade in DB
        if (!latestDBObjects.isEmpty()) {
            TradesFullLayoutRecord latestFullLayoutRecord =  latestDBObjects.get(0);
            LOG.info("Got latest full layout trade from the DB: " + latestFullLayoutRecord);
            if (calendar.getTimeInMillis() > latestFullLayoutRecord.getTimestamp()) {
                LOG.info("Last trade is more than 1 day old, returning -ONE_DAY timestamp");
            } else {
                LOG.info("Last trade is less that 1 day old, returning the time of the last trade");
            }
            return Math.max(calendar.getTimeInMillis(), latestDBObjects.get(0).getTimestamp());
        } else {
            LOG.info("There are no trades in the DB, returning -ONE_DAY timestamp");
            return calendar.getTimeInMillis();
        }
    }

    private List<TradesHistoryRecord> readCSV(Markets market, Currency currency) {
        Date before = new Date();

        //create the name of the CSV out of the market and currency
        String csvFile = "historical_data/" + getMarketIdentifierName(market, currency) + ".csv";
        LOG.info("Start reading CSV file: " + csvFile);

        BufferedReader br = null;
        String output = "";

        List<TradesHistoryRecord> result = new ArrayList<>();

        try {

            br = new BufferedReader(new FileReader(new File(Resources.getResource(csvFile).getFile())));
            while ((output = br.readLine()) != null) {
                TradesHistoryRecord csvRecord = extractCSVRecord(output);
                if (csvRecord != null) {
                    result.add(csvRecord);
                }
            }

        } catch (Exception e) {
            LOG.error("An error has occurred while reading CSV: " + csvFile, e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    LOG.error("IO Error while closing CSV: " + csvFile, e);
                }
            }
        }

        LOG.info("Done reading CSV:" + csvFile + ", operation took: " + (new Date().getTime() - before.getTime()) + " ms");

        return result;
    }

    private TradesHistoryRecord extractCSVRecord(String line) {
        try {
            // use comma as separator
            String[] tradeLine = line.split(CSV_SPLITTER);
            TradesHistoryRecord trade = new TradesHistoryRecord(Long.valueOf(tradeLine[0]), Double.valueOf(tradeLine[1]), Double.valueOf(tradeLine[2]));
            return trade;
        } catch (Exception e) {
            LOG.warn("ignoring malformed line:" + line);
        }

        return null;
    }

    private List<TradesHistoryRecord> executeQuery(Markets market, Currency currency, Long initialDate) {
        HttpURLConnection connection = null;

        List<TradesHistoryRecord> result = new ArrayList<>();

        try {
            result = executeGetRequest(connection, market, currency, initialDate);
        }
        //Capture Exceptions
        catch (IllegalStateException ex) {
            LOG.error("An error has occurred while executing a trades API request", ex);
        } catch (IOException ex) {
            LOG.error("An error has occurred while executing a trades API request", ex);
        } finally {
            //close the connection, set all objects to null
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return result;
    }

    private List<TradesHistoryRecord> executeGetRequest(HttpURLConnection connection, Markets market, Currency currency, Long initialDate) throws IOException {
        boolean httpError = false;

        HashMap<String, String> args = new LinkedHashMap<>();
        args.put("symbol", market.name().toLowerCase() + currency.name());
        args.put("start", Long.toString(initialDate));

        String getData = buildQueryString(args);
        //http://api.bitcoincharts.com/v1/trades.csv?symbol=btcexYAD&start=1303100000
        // build URL
        URL queryUrl = new URL("http://api.bitcoincharts.com/v1/trades.csv" + "?" + getData);

        connection = (HttpURLConnection) queryUrl.openConnection();
        connection.setRequestMethod("GET");

        return printErrorOrReturnResult(connection, httpError, getData);
    }

    private List<TradesHistoryRecord> printErrorOrReturnResult(HttpURLConnection connection, boolean httpError, String post_data) throws IOException {
        List<TradesHistoryRecord> result = new ArrayList<>();
        BufferedReader br;
        if (connection.getResponseCode() >= 400) {
            httpError = true;//TODO , if HTTP error, do something else with output!
            br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
        } else {
            br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        }

        String output;

        if (httpError)
            LOG.error("Post Data: " + post_data);
        if (printHttpResponse)
            LOG.info("HTTP response : \n"); //do not log unless is error > 400
        while ((output = br.readLine()) != null) {
            TradesHistoryRecord csvRecord = extractCSVRecord(output);
            if (csvRecord != null) {
                result.add(csvRecord);
            }
        }
        return result;
    }

    public static String getMarketIdentifierName(Markets market, Currency currency) {
        return market.name().toLowerCase() + currency.name();
    }

    public ConcurrentHashMap<MultiKey, Long> getPreviousTimestampMap() {
        return previousTimestampMap;
    }

    //Build the query string given a set of query parameters
    private String buildQueryString(HashMap<String, String> args) {
        String result = new String();
        for (String hashkey : args.keySet()) {
            if (result.length() > 0) result += '&';
            try {
                result += URLEncoder.encode(hashkey, ENCODING) + "="
                        + URLEncoder.encode(args.get(hashkey), ENCODING);
            } catch (Exception ex) {
                LOG.error("An error has occurred while building query string for:" + args, ex);
            }
        }
        return result;
    }
}
