package bitcoinGWT.server.ticker;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/11/14
 * Time: 12:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class TradeEngineMarketLoader {


    private ExecutorService threadPool;

    public TradeEngineMarketLoader() {
        threadPool = Executors.newFixedThreadPool(30);
    }
}
