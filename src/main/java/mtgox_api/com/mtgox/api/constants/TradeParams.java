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

    public static String LAST = "last";
    public static String LAG = "lag_text";

    public static String VALUE = "value";

    //"date":1364767201,"price":"92.65","amount":"0.47909825","price_int":"9265000","amount_int":"47909825",
    // "tid":"1364767201381791","price_currency":"USD","item":"BTC",
    // "trade_type":"bid","primary":"Y","properties":"limit"
    public enum Trades {
        date, price, amount, price_currency, item, trade_type;
    }

}
