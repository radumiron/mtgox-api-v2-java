package bitcoinGWT.server;

import org.apache.log4j.Logger;
import trading.mtgox_api.com.mtgox.api.ApiKeys;
import trading.mtgox_api.com.mtgox.examples.utils.Utils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/24/13
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradeUtils {

    private static final Logger LOG = Logger.getLogger(TradeUtils.class);


    //readApiKeysFromFile
    public static ApiKeys readApiKeys(String pathToJsonFile) {
        //see https://code.google.com/p/json-simple/wiki/DecodingExamples
        JSONParser parser=new JSONParser();
        ApiKeys apiKeys = null;
        String apiStr = Utils.readFromFile(pathToJsonFile);
        try {
            JSONObject obj2=(JSONObject)(parser.parse(apiStr));
            apiKeys= new ApiKeys((String)obj2.get("mtgox_secret_key"), (String)obj2.get("mtgox_api_key"));
        } catch (ParseException ex) {
            LOG.error(ex);
        }
        return apiKeys;
    }
}
