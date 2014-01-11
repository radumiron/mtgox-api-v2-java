package history;

import bitcoinGWT.server.dao.MongoDAO;
import bitcoinGWT.server.dao.entities.TradesCSVRecord;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import com.google.common.io.Resources;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/11/14
 * Time: 1:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class HistoryDownloader {

    private static final String ENCODING = "UTF-8";
    private static final String CSV_SPLITTER = ",";

    private boolean printHttpResponse = true;

    public static void main(String[] args) {
        new HistoryDownloader().performTradesImport();
    }

    private void performTradesImport() {
        Markets demoMarket = Markets.MTGOX;
        Currency demoCurrency = Currency.EUR;

        //first, populate the MongoDB schema with historical data read from the CSV file
        //todo
        MongoDAO dao = new MongoDAO();

        List<TradesCSVRecord> csvRecords = readCSV(demoMarket, demoCurrency);
        dao.saveCSVRecords(csvRecords, false);

        //last record got from the CSV, this will be the threshold of getting the trades from the bitcoincharts API
        TradesCSVRecord latestCSVRecord = csvRecords.get(csvRecords.size() - 1);

        //then, find data of newest record in the DB
        //todo
        //use a demo date until then
        //Calendar startDate = GregorianCalendar.getInstance();
        //startDate.roll(Calendar.YEAR, -2);


        //then, try to load all the trades from bitcoincharts, which happened between the last record in the DB and
        //as close as possible to the current time (the bitcoincharts API returns trades with a latency of ~ 15 minutes)

        List<TradesCSVRecord> apiCSVRecords = executeQuery(demoMarket, demoCurrency, latestCSVRecord.getTime());

        //save the API records in the DB
        dao.saveCSVRecords(apiCSVRecords, true);

        //then, we'll have to use the XChange machine to load the latest data into the MongoDB schema
        //todo

        //then, we'll load just the last X trades (or time based threshold) into the application memory, for quick access
        //todo
    }

    private List<TradesCSVRecord> readCSV(Markets market, Currency currency) {
        Date before = new Date();

        //create the name of the CSV out of the market and currency
        String csvFile = "historical_data/" + market.name().toLowerCase() + currency.name() + ".csv";
        System.out.println("Start reading CSV file: " + csvFile);

        BufferedReader br = null;
        String output = "";

        List<TradesCSVRecord> result = new ArrayList<>();

        try {

            br = new BufferedReader(new FileReader(new File(Resources.getResource(csvFile).getFile())));
            while ((output = br.readLine()) != null) {
                TradesCSVRecord csvRecord = extractCSVRecord(output);
                if (csvRecord != null) {
                    result.add(csvRecord);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println("Done reading CSV:" + csvFile + ", operation took: " + (new Date().getTime() - before.getTime()) + " ms");

        return result;
    }

    private TradesCSVRecord extractCSVRecord(String line) {
        try {
            // use comma as separator
            String[] tradeLine = line.split(CSV_SPLITTER);
            TradesCSVRecord trade = new TradesCSVRecord(Long.valueOf(tradeLine[0]), Double.valueOf(tradeLine[1]), Double.valueOf(tradeLine[2]));
            return trade;
        } catch (Exception e) {
            System.out.println("ignoring malformed line:" + line);
            e.printStackTrace();
        }

        return null;
    }

    private List<TradesCSVRecord> executeQuery(Markets market, Currency currency, Long initialDate) {
        HttpURLConnection connection = null;

        List<TradesCSVRecord> result = new ArrayList<>();

        try {
            result = executeGetRequest(connection, market, currency, initialDate);
        }
        //Capture Exceptions
        catch (IllegalStateException ex) {
            ex.printStackTrace();
            //System.err.println(ex);
        } catch (IOException ex) {
            //System.err.println(ex);
            ex.printStackTrace();
        } finally {
            //close the connection, set all objects to null
            if (connection != null) {
                connection.disconnect();
                connection = null;
            }
        }
        return result;
    }

    private List<TradesCSVRecord> executeGetRequest(HttpURLConnection connection, Markets market, Currency currency, Long initialDate) throws IOException {
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

    private List<TradesCSVRecord> printErrorOrReturnResult(HttpURLConnection connection, boolean httpError, String post_data) throws IOException {
        List<TradesCSVRecord> result = new ArrayList<>();
        BufferedReader br;
        if (connection.getResponseCode() >= 400) {
            httpError = true;//TODO , if HTTP error, do something else with output!
            br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
        } else {
            br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
        }

        String output;

        if (httpError)
            System.err.println("Post Data: " + post_data);
        if (printHttpResponse)
            System.out.println("HTTP response : \n"); //do not log unless is error > 400
        while ((output = br.readLine()) != null) {
            TradesCSVRecord csvRecord = extractCSVRecord(output);
            if (csvRecord != null) {
                result.add(csvRecord);
            }
        }
        return result;
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
                ex.printStackTrace();
            }
        }
        return result;
    }
}