package mtgox_api.com.mtgox.api.constants;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/24/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradeParams {

    public static String DATA = "data";

   /* *{
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
        *}*/


    public static String LAST = "last";
    public static String HIGH = "high";
    public static String LOW = "low";
    public static String AVERAGE = "avg";
    public static String VWAP = "vwap";
    public static String VOLUME = "vol";
    public static String LAST_LOCAL = "last_local";
    public static String LAST_ORIGINAL = "last_orig";
    public static String LAST_ALL = "last_all";
    public static String BID = "buy";
    public static String ASK = "sell";
    public static String NOW = "now";

    public static String LAG = "lag_text";

    public static String VALUE = "value";

    //"date":1364767201,"price":"92.65","amount":"0.47909825","price_int":"9265000","amount_int":"47909825",
    // "tid":"1364767201381791","price_currency":"USD","item":"BTC",
    // "trade_type":"bid","primary":"Y","properties":"limit"
    public enum Trades {
        date, price, amount, price_currency, item, trade_type;
    }

}
