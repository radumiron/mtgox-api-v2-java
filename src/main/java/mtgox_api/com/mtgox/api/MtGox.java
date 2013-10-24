/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mtgox_api.com.mtgox.api;


import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.HttpsURLConnection;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TickerFullLayoutObject;
import bitcoinGWT.shared.model.TickerShallowObject;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import mtgox_api.com.mtgox.api.constants.TradeParams;
import mtgox_api.com.mtgox.examples.utils.Utils;
import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Component;

/**
 * https://github.com/adv0r/mtgox-apiv2-java
 *
 * @author adv0r <leg@lize.it>
 *         MIT License (see LICENSE.md)
 *         Implementation of MtGox Api V2
 *         Consider donations @ 1N7XxSvek1xVnWEBFGa5sHn1NhtDdMhkA7
 *         unofficial documentation by nitrous https://bitbucket.org/nitrous/mtgox-api/overview
 */
@Component
public class MtGox implements TradeInterface {

    public static final String MONEY_TICKER = "/MONEY/TICKER";

    public enum RequestType {GET, POST}

    ;

    private ApiKeys keys;

    private HashMap<Currency, Integer> devisionFactors;


    private final double MIN_ORDER = 0.01; //BTC

    private final String API_BASE_URL = "https://data.mtgox.com/api/2/";

    //Paths
    private final String API_GET_INFO = "MONEY/INFO";

    private final String API_WITHDRAW = "MONEY/BITCOIN/SEND_SIMPLE";
    private final String API_LAG = "MONEY/ORDER/LAG";
    private final String API_ADD_ORDER = "BTCUSD/MONEY/ORDER/ADD";

    private final String SIGN_HASH_FUNCTION = "HmacSHA512";
    private final String ENCODING = "UTF-8";

    private boolean printHttpResponse;

    public MtGox() {
        Utils.initSSL();
        ApiKeys keys = Utils.readApiKeys("WEB-INF/res/api-keys.json");

        initTrade(keys);
    }

    private void initTrade(ApiKeys keys) {
        this.keys = keys;
        printHttpResponse = false;
        // set division Factors
        devisionFactors = new HashMap<Currency, Integer>();
        devisionFactors.put(Currency.BTC, 100000000);
        devisionFactors.put(Currency.USD, 100000);
        devisionFactors.put(Currency.GBP, 100000);
        devisionFactors.put(Currency.EUR, 100000);
        devisionFactors.put(Currency.JPY, 1000);
        devisionFactors.put(Currency.AUD, 100000);
        devisionFactors.put(Currency.CAD, 100000);
        devisionFactors.put(Currency.CHF, 100000);
        devisionFactors.put(Currency.CNY, 100000);
        devisionFactors.put(Currency.DKK, 100000);
        devisionFactors.put(Currency.HKD, 100000);
        devisionFactors.put(Currency.PLN, 100000);
        devisionFactors.put(Currency.RUB, 100000);
        devisionFactors.put(Currency.SEK, 1000);
        devisionFactors.put(Currency.SGD, 100000);
        devisionFactors.put(Currency.THB, 100000);

    }

    public void setPrintHTTPResponse(boolean resp) {
        this.printHttpResponse = resp;
    }

    @Override
    public String withdrawBTC(double amount, String dest_address) {  //TODO
        String urlPath = API_WITHDRAW;
        HashMap<String, String> query_args = new HashMap<>();
        /*Params
         * address : Target bitcoin address
         * amount_int : Amount of bitcoins to withdraw
         * fee_int : Fee amount to be added to transaction (optional), maximum 0.01 BTC
         * no_instant : Setting this parameter to 1 will prevent transaction from being processed internally, and force usage of the bitcoin blockchain even if receipient is also on the system
         * green : Setting this parameter to 1 will cause the TX to use MtGox’s green address
         */
        query_args.put("amount_int", Long.toString(Math.round(amount * devisionFactors.get(Currency.BTC))));
        query_args.put("address", dest_address);
        String queryResult = query(urlPath, query_args);


        /*Sample result
        * On success, this method will return the transaction id (in offser trx ) which will contain either the bitcoin transaction id as hexadecimal or a UUID value in case of internal transfer.
        */

        JSONParser parser = new JSONParser();
        try {
            JSONObject obj2 = (JSONObject) (parser.parse(queryResult));
            //JSONObject data = (JSONObject)obj2.get("data"); //TODO


        } catch (ParseException ex) {
            Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ""; //TODO Edit
    }

    @Override
    public String sellBTC(double amount) {
        return placeOrder("sell", Math.round(amount * devisionFactors.get(Currency.BTC)));
    }

    @Override
    public String buyBTC(double amount) {
        return placeOrder("buy", Math.round(amount * devisionFactors.get(Currency.BTC)));
    }

    public String placeOrder(String type, long amount_int) {

        String toReturn = "";
        String result = "";
        String data = "";
        String urlPath = API_ADD_ORDER;
        HashMap<String, String> query_args = new HashMap<>();
        /*Params
         * type : {ask (sell) | bid(buy) }
         * amount_int : amount of BTC to buy or sell, as an integer
         * price_int : The price per bitcoin in the auxiliary currency, as an integer, optional if you wish to trade at the market price
         */
        query_args.put("amount_int", Long.toString(amount_int));
        if (type.equals("sell"))
            query_args.put("type", "ask");
        else
            query_args.put("type", "bid");

        String queryResult = query(urlPath, query_args);
        /*Sample result
        * {"result":"success","data":"abc123-def45-.."}
        */
        JSONParser parser = new JSONParser();
        try {
            JSONObject obj2 = (JSONObject) (parser.parse(queryResult));
            result = (String) obj2.get("result");
            data = (String) obj2.get("data");

            //lastPriceArray[0] = (Double)obj2.get("last"); //USD


        } catch (ParseException ex) {
            Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (result.equals("success")) {
            toReturn = "executed : " + data;
        } else
            toReturn = "not executed : " + data; //TODO test this branch

        return toReturn; //TODO change
    }

    @Override
    public double[] getBalance() {
        String urlPath = API_GET_INFO;
        HashMap<String, String> query_args = new HashMap<>();

        /*Params
        *
        */
        double[] balanceArray = new double[3];


        String queryResult = query(urlPath, query_args);
        /*Sample result
        * {
        *   "data": {
        *       "Created": "yyyy-mm-dd hh:mm:ss",
        *       "Id": "abc123",
        *       "Index": "123",
        *       "Language": "en_US",
        *       "Last_Login": "yyyy-mm-dd hh:mm:ss",
        *       "Login": "username",
        *       "Monthly_Volume":                   **Currency Object**,
        *       "Trade_Fee": 0.6,
        *       "Rights": ['deposit', 'get_info', 'merchant', 'trade', 'withdraw'],
        *       "Wallets": {
        *           "BTC": {
        *               "Balance":                  **Currency Object**,
        *               "Daily_Withdraw_Limit":     **Currency Object**,
        *               "Max_Withdraw":             **Currency Object**,
        *               "Monthly_Withdraw_Limit": null,
        *               "Open_Orders":              **Currency Object**,
        *               "Operations": 1,
        *           },
        *           "USD": {
        *               "Balance":                  **Currency Object**,
        *               "Daily_Withdraw_Limit":     **Currency Object**,
        *               "Max_Withdraw":             **Currency Object**,
        *               "Monthly_Withdraw_Limit":   **Currency Object**,
        *               "Open_Orders":              **Currency Object**,
        *               "Operations": 0,
        *           },
        *           "JPY":{...}, "EUR":{...},
        *           // etc, depends what wallets you have
        *       },
        *   },
        *   "result": "success"
        * }
        */

        JSONParser parser = new JSONParser();
        try {
            JSONObject httpAnswerJson = (JSONObject) (parser.parse(queryResult));
            JSONObject dataJson = (JSONObject) httpAnswerJson.get("data");
            JSONObject walletsJson = (JSONObject) dataJson.get("Wallets");

            JSONObject BTCwalletJson = (JSONObject) ((JSONObject) walletsJson.get("BTC")).get("Balance");

            String BTCBalance = (String) BTCwalletJson.get("value");

            boolean hasDollars = true;
            boolean hasEuros = true;
            JSONObject USDwalletJson, EURwalletJson;
            String USDBalance = "", EURBalance = "";

            try {
                USDwalletJson = (JSONObject) ((JSONObject) walletsJson.get("USD")).get("Balance");
                USDBalance = (String) USDwalletJson.get("value");
            } catch (Exception e) {
                hasDollars = false;
            }

            try {
                EURwalletJson = (JSONObject) ((JSONObject) walletsJson.get("EUR")).get("Balance");
                EURBalance = (String) EURwalletJson.get("value");
            } catch (Exception e) {
                hasEuros = false;
            }

            balanceArray[0] = Double.parseDouble(BTCBalance); //BTC

            if (hasDollars)
                balanceArray[1] = Double.parseDouble(USDBalance); //USD
            else
                balanceArray[1] = -1; //Account does not have USD wallet

            if (hasEuros)
                balanceArray[2] = Double.parseDouble(EURBalance); //EUR
            else
                balanceArray[2] = -1; //Account does not have EUR wallet


        } catch (ParseException ex) {
            Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
        }

        return balanceArray;
    }


    public String query(String path, HashMap<String, String> args) {
        return query(RequestType.POST, path, args);
    }

    public String query(RequestType requestType, String path, HashMap<String, String> args) {
        GoxService query = new GoxService(requestType, path, args, keys);
        String queryResult = query.executeQuery();
        return queryResult;
        //TODO should be done by a different thread ...
    }

    public <T extends TickerShallowObject> T getLastPrice(Currency cur) {
        return getPrice(cur, false);
    }

    public <T extends TickerShallowObject> T getPrice(Currency currency) {
        return getPrice(currency, true);
    }

    private <T extends TickerShallowObject> T getPrice(Currency currency, boolean fullLayoutObject) {

        String urlPath = getTickerPath(currency, !fullLayoutObject);
        long divideFactor = devisionFactors.get(currency);
        HashMap<String, String> query_args = new HashMap<>();

        /*Params :
        * No params required
        */
        System.out.println(new Date() + ": executing ticker query");
        String queryResult = query(urlPath, query_args);
        System.out.println(new Date() + ": after ticker query");
        /* Result sample :
        *{
        *   "result":"success",
        *   "data": {
        *       "high":       **Currency Object - USD**,
        *       "low":        **Currency Object - USD**,
        *       "avg":        **Currency Object - USD**,
        *       "vwap":       **Currency Object - USD**,
        *       "vol":        **Currency Object - BTC**,
        *       "last_local": **Currency Object - USD**,
        *       "last_orig":  **Currency Object - ???**,
        *       "last_all":   **Currency Object - USD**,
        *       "last":       **Currency Object - USD**,
        *       "buy":        **Currency Object - USD**,
        *       "sell":       **Currency Object - USD**,
        *       "now":        "1364689759572564"
        *   }
        *}
        */
        System.out.println(new Date() + ": parsing ticker results");
        JSONParser parser = new JSONParser();
        TickerShallowObject result = null;
        try {
            JSONObject httpAnswerJson = (JSONObject) (parser.parse(queryResult));
            JSONObject dataJson = (JSONObject) httpAnswerJson.get(TradeParams.DATA);
            double last = getDoubleJSONValue(dataJson, TradeParams.LAST);
            Date now = new Date(Long.parseLong((String) dataJson.get(TradeParams.NOW)) / 1000); //MTGOX gives microsecond, we need normal seconds
            if (fullLayoutObject) {
                double high = getDoubleJSONValue(dataJson, TradeParams.HIGH);
                double low = getDoubleJSONValue(dataJson, TradeParams.LOW);
                double average = getDoubleJSONValue(dataJson, TradeParams.AVERAGE);
                double vwap = getDoubleJSONValue(dataJson, TradeParams.VWAP);
                double volume = getDoubleJSONValue(dataJson, TradeParams.VOLUME);
                double lastLocal = getDoubleJSONValue(dataJson, TradeParams.LAST_LOCAL);
                //TODO still don't need this, it returns the original value in $, not depending on the currency
                //double lastOrig = getDoubleJSONValue(dataJson, TradeParams.LAST_ORIGINAL);
                double lastAll = getDoubleJSONValue(dataJson, TradeParams.LAST_ALL);
                double bid = getDoubleJSONValue(dataJson, TradeParams.BID);
                double ask = getDoubleJSONValue(dataJson, TradeParams.ASK);
                result = new TickerFullLayoutObject(currency, last, now, ask, average, bid, high, lastAll, lastLocal, -1, low, volume, vwap);
            } else {
                result = new TickerShallowObject(currency, last, now);
            }
        } catch (ParseException ex) {
            Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(new Date() + ": after parsing ticker result");
        return (T) (result != null ? result : new TickerShallowObject(currency, 0, null));
    }

    private String getStringJSONValue(JSONObject object, String params) {
        return (String) object.get(params);
    }

    private Double getDoubleJSONValue(JSONObject object, String params) {
        double value = 0;
        try {
            JSONObject lastJson = (JSONObject) object.get(params);
            value = Double.parseDouble((String) lastJson.get(TradeParams.VALUE));
        } catch (Exception e) {
            //todo
        }

        return value;
    }

    private Long getLongJSONValue(JSONObject object, String params) {
        long value = 0;
        try {
            JSONObject lastJson = (JSONObject) object.get(params);
            value = Long.parseLong((String) lastJson.get(TradeParams.VALUE));
        } catch (Exception e) {
            //todo
        }

        return value;
    }

    private Date getDateJSONValue(JSONObject object, String params) {
        Date value = null;
        try {
            JSONObject lastJson = (JSONObject) object.get(params);
            value = new Date(Long.parseLong((String) lastJson.get(TradeParams.VALUE)));
        } catch (Exception e) {
            //todo
        }

        return value;
    }

    @Override
    public String getLag() {
        String urlPath = API_LAG;
        HashMap<String, String> query_args = new HashMap<>();
        /*Params
        *
        */
        String queryResult = query(urlPath, query_args);
        /*Sample result
        * the lag in milliseconds
        */
        JSONParser parser = new JSONParser();
        String lag = "";
        try {
            JSONObject httpAnswerJson = (JSONObject) (parser.parse(queryResult));
            JSONObject dataJson = (JSONObject) httpAnswerJson.get(TradeParams.DATA);
            lag = (String) dataJson.get(TradeParams.LAG);
        } catch (ParseException ex) {
            Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lag;
    }

    @Override
    public List<TradesFullLayoutObject> getTrades(Currency currency, long previousTimestamp) {
        String urlPath = getTradesPath(currency);
        HashMap<String, String> query_args = new HashMap<>();

        query_args.put("since", Long.toString(previousTimestamp) + "000");


        System.out.println(new Date() + ": executing trades query");
        String queryResult = query(RequestType.GET, urlPath, query_args);
        System.out.println(new Date() + ": after trades query");


        System.out.println(new Date() + ": parsing trades results");
        List<TradesFullLayoutObject> trades = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {
            JSONObject httpAnswerJson = (JSONObject) (parser.parse(queryResult));
            //getting the trades array
            JSONArray dataJson = (JSONArray) httpAnswerJson.get(TradeParams.DATA);
            Iterator<JSONObject> it = dataJson.iterator();
            if (!it.hasNext()) {
                System.out.println(new Date() + ": there are no new results since: " + new Date(previousTimestamp));
            }
            //going through every trad
            while (it.hasNext()) {
                JSONObject jsonTrade = it.next();

                long tradeId = Long.parseLong((String) jsonTrade.get(TradeParams.Trades.tid.toString()));
                long date = tradeId / 1000; //in order to get the amount in milliseconds (converted from microseconds)
                Double price = Double.parseDouble((String) jsonTrade.get(TradeParams.Trades.price.toString()));
                Double amount = Double.parseDouble((String) jsonTrade.get(TradeParams.Trades.amount.toString()));
                Currency priceCurrency = Currency.valueOf((String) jsonTrade.get(TradeParams.Trades.price_currency.toString()));
                Currency item = Currency.valueOf((String) jsonTrade.get(TradeParams.Trades.item.toString()));
                TradesFullLayoutObject.TradeType tradeType = TradesFullLayoutObject.TradeType
                        .valueOf((String) jsonTrade.get(TradeParams.Trades.trade_type.toString()));

                TradesFullLayoutObject trade = new TradesFullLayoutObject(tradeId, new Date(date), price, amount, priceCurrency, item, tradeType);
                //don't return trades with amounts under 0.01
                if (amount >= 0.01) {
                    trades.add(trade);
                } else {
                    Logger.getLogger(MtGox.class.getName()).info("Retrieved trade under 0.01BTC, tradeId = " + tradeId);
                }

            }
        } catch (Exception ex) {
            Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(new Date() + ": after parsing trades result");

        return trades;
    }

    private class GoxService {
        protected String path;
        protected HashMap args;
        protected ApiKeys keys;
        protected RequestType requestType;


        public GoxService(RequestType requestType, String path, HashMap<String, String> args, ApiKeys keys) {
            this.requestType = requestType;
            this.path = path;
            this.args = args;
            this.keys = keys;
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
                    Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return result;
        }

        private String signRequest(String secret, String hash_data) {
            String signature = "";
            try {
                Mac mac = Mac.getInstance(SIGN_HASH_FUNCTION);
                SecretKeySpec secret_spec = new SecretKeySpec(Base64.decodeBase64(secret), SIGN_HASH_FUNCTION);
                mac.init(secret_spec);
                signature = Base64.encodeBase64String(mac.doFinal(hash_data.getBytes()));
            } catch (NoSuchAlgorithmException | InvalidKeyException e) {
                Logger.getLogger(MtGox.class.getName()).log(Level.SEVERE, null, e);
            }
            return signature;
        }

        private String executeGetRequest(HttpsURLConnection connection) throws IOException {
            boolean httpError = false;

            String getData = buildQueryString(args);

            // build URL
            URL queryUrl = new URL(API_BASE_URL + path + "?" + getData);

            connection = (HttpsURLConnection) queryUrl.openConnection();
            connection.setRequestMethod(requestType.name());

            return printErrorOrReturnResult(connection, httpError, getData);
        }

        private String executePostRequest(HttpsURLConnection connection) throws IOException {
            boolean httpError = false;

            String nonce = String.valueOf(System.currentTimeMillis()) + "000";
            // add nonce and build arg list
            args.put("nonce", nonce);
            String post_data = buildQueryString(args);
            String hash_data = path + "\0" + post_data; //Should be correct

            // args signature with apache cryptografic tools
            String signature = signRequest(keys.getPrivateKey(), hash_data);

            // build URL
            URL queryUrl = new URL(API_BASE_URL + path);
            // create and setup a HTTP connection
            connection = (HttpsURLConnection) queryUrl.openConnection();

            connection.setRequestMethod(requestType.name());

            connection.setRequestProperty("User-Agent", "Advanced-java-client API v2");
            connection.setRequestProperty("Rest-Key", keys.getApiKey());
            connection.setRequestProperty("Rest-Sign", signature.replaceAll("\n", ""));

            connection.setDoOutput(true);
            connection.setDoInput(true);

            //Read the response

            DataOutputStream os = new DataOutputStream(connection.getOutputStream());
            os.writeBytes(post_data);
            os.close();

            return printErrorOrReturnResult(connection, httpError, post_data);
        }

        private String printErrorOrReturnResult(HttpsURLConnection connection, boolean httpError, String post_data) throws IOException {
            String answer = "";
            BufferedReader br = null;
            boolean toLog = false;
            if (connection.getResponseCode() >= 400) {
                httpError = true;//TODO , if HTTP error, do something else with output!
                br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));
                toLog = true;
            } else {
                br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            }

            String output;

            if (httpError)
                System.err.println("Post Data: " + post_data);
            if (printHttpResponse)
                System.out.println("Query to :" + path + " , HTTP response : \n"); //do not log unless is error > 400
            while ((output = br.readLine()) != null) {
                if (printHttpResponse)
                    System.out.println(output);
                answer += output;
            }
            return answer;
        }

        private String executeQuery() {
            String answer = "";

            HttpsURLConnection connection = null;

            try {
                switch (requestType) {
                    case POST:
                        return executePostRequest(connection);
                    case GET:
                        return executeGetRequest(connection);
                }
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
            return answer;
        }
    }

    /**
     * returns the string used to get the Ticker
     *
     * @return the string you're searching for;)
     */
    private String getTickerPath(Currency cur, boolean fast) {
        return Currency.BTC + cur.toString() + MONEY_TICKER + (fast ? "_FAST" : "");
    }

    private String getTradesPath(Currency cur) {
        return Currency.BTC + cur.toString() + "/MONEY/TRADES/FETCH";
    }


}