package bitcoinGWT.server;

import mtgox_api.com.mtgox.api.ApiKeys;
import mtgox_api.com.mtgox.examples.utils.Utils;
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
            System.err.println(ex);
        }
        return apiKeys;
    }

    public static void initSSL() {
        // SSL Certificates  trustStore ----------------------------------------
        //Set the SSL certificate for mtgox - Read up on Java Trust store.
        //System.setProperty("javax.net.ssl.trustStore","./res/ssl/mtgox.jks");
        System.setProperty("javax.net.ssl.trustStore", "res/ssl/mtgox.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "h4rdc0r_"); //I encripted the jks file using this pwd
        //System.setProperty("javax.net.debug","ssl"); //Uncomment for debugging SSL errors
    }
}
