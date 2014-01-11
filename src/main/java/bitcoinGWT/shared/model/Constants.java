package bitcoinGWT.shared.model;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/3/13
 * Time: 12:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class Constants {
    public static int TICKER_INTERVAL = 5000;
    //public static int TICKER_INTERVAL = 60000 * 10;
    public static int TRADES_INTERVAL = 30000;
    //TODO increase this to 7 days (1 week)
    public static final int INITIAL_TRADES_INTERVAL = 24/*12*//*3*/ *  60 * 60 * 1000; //last 48 hours
    public static final int TRADES_RETRIEVAL_INTERVAL = 5 * 1000;//5 seconds

    public static final int CANDLESTICK_CHART_TRADES_RETRIEVAL_INTERVAL = 1 * 60 * 1000;//1 minute

    public static final int TRADES_SIZE = 5000;

    public static int INITIAL_UI_TRADES_DELAY = 5000;
    public static int TRADES_GRID_UI_BUFFER = 200;
}
