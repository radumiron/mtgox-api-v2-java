package bitcoinGWT.server.ticker;

import bitcoinGWT.server.converter.TradesConverter;
import bitcoinGWT.server.dao.GenericDAO;
import bitcoinGWT.server.dao.entities.TradesFullLayoutRecord;
import bitcoinGWT.server.dao.entities.TradesHistoryRecord;
import bitcoinGWT.server.history.HistoryDownloader;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.google.web.bindery.requestfactory.server.Pair;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import trading.api_interfaces.TradeInterface;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static bitcoinGWT.shared.model.Constants.INITIAL_TRADES_INTERVAL;
import static bitcoinGWT.shared.model.Constants.TRADES_RETRIEVAL_INTERVAL;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/12/14
 * Time: 11:23 AM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Primary
@Qualifier("GENERIC")
public class GenericTradesEngine extends TradesEngine {

    private static final Logger LOG = Logger.getLogger(GenericTradesEngine.class);

    @Autowired
    @Qualifier("MONGO")
    private GenericDAO dao;

    @Autowired
    @Qualifier("XChange")
    private TradeInterface trade;

    @Autowired
    HistoryDownloader historyDownloader;

    //private ExecutorService executor;

    private ConcurrentHashMap<MultiKey, Boolean> shouldLoadTradesMap;

    private ConcurrentHashMap<MultiKey, Long> previousTimestampMap;

    @PostConstruct
    private void init() {
        LOG.info("init of GenericTradeEngine");
        //todo put more threads here
        //executor = Executors.newFixedThreadPool();
        //executor = Executors.newSingleThreadExecutor();
        shouldLoadTradesMap = new ConcurrentHashMap<>();
        previousTimestampMap = historyDownloader.getPreviousTimestampMap();
    }

    @Override
    protected void executeTradeTask() {
        //empty, since the load of the trades will happen only when the ticker changes
    }

    public void loadAndSaveTradesPerMarketAndCurrency(Markets market, Currency currency) {
        List<TradesFullLayoutObject> sortedTrades = new ArrayList<>(trade.getTrades(market, currency, getPreviousTimestamp(market, currency)));

        if (sortedTrades.size() > 0) {
            //save the new trades in the database
            Pair<String, List<TradesFullLayoutRecord>> recordsToSave = new Pair<>(HistoryDownloader.getMarketIdentifierName(market, currency),
                    TradesConverter.convertTradesFullLayoutObjectsToTradesFullLayoutRecords(sortedTrades));
            Collection<TradesFullLayoutRecord> actuallySavedTrades = dao.saveTradesFullLayoutRecords(recordsToSave, true);

            //save also data for the history (for the chart)
            Pair<String, List<TradesHistoryRecord>> historyRecordsToSave = new Pair<>(HistoryDownloader.getMarketIdentifierName(market, currency),
                    TradesConverter.convertTradesFullLayoutRecordsToTradesHistoryRecords(actuallySavedTrades));
            dao.saveTradesHistoryRecords(historyRecordsToSave, true);

            //TODO save also data for the candlestick charts

            LOG.info("New trades loaded, size of loaded trades=" + sortedTrades.size());
            //in case the list downloaded is NOT empty, mark that all who will ask for trades will be able to download the new list
            shouldLoadTradesMap.put(new MultiKey(market, currency), true);
        } else {
            //in case the list is empty,
            shouldLoadTradesMap.put(new MultiKey(market, currency), false);
        }
    }

    private List<TradesFullLayoutObject> getAllTrades(Markets market, Currency currency) {
        return getTrades(market, currency, null);
    }

    private List<TradesFullLayoutObject> getTrades(Markets market, Currency currency, Long timestamp) {
        List<TradesFullLayoutRecord> records = dao.getTradesFullLayoutRecords(HistoryDownloader.getMarketIdentifierName(market, currency), timestamp, false);
        return TradesConverter.convertTradesFullLayoutRecordsToTradesFullLayoutObjects(records);
    }

    private long getPreviousTimestamp(Markets market, Currency currency) {
        Long previousTimestamp = previousTimestampMap.get(new MultiKey(market, currency));
        Calendar calendar = GregorianCalendar.getInstance();
        //set the time when this method was previously executed
        if (previousTimestamp == null) {    //if first download of trades
            //initialize previousTimestamp with the current time
            previousTimestamp = calendar.getTimeInMillis();
            previousTimestampMap.put(new MultiKey(market, currency), previousTimestamp);
            LOG.info("first time trades");

            LOG.info("current time: " + new Date(previousTimestamp));
            //go back the specified amount of seconds
            calendar.add(Calendar.MILLISECOND, (-1) * INITIAL_TRADES_INTERVAL);

            return calendar.getTimeInMillis();
        } else {
            //save the current run time as a reference for the next run time.
            Long currentExecutionTime = calendar.getTimeInMillis();
            previousTimestampMap.put(new MultiKey(market, currency), currentExecutionTime);
            LOG.info("getting trades from: " + new Date(previousTimestamp) + " to " + new Date(currentExecutionTime));
            return previousTimestamp;
        }
    }

    @Override
    protected int getTimerInterval() {
        return TRADES_RETRIEVAL_INTERVAL;
    }

    protected String getTradeName() {
        return this.getClass().getName();
    }

    @Override
    public Set<Currency> getSupportedCurrencies(Markets market) {
        return trade.getSupportedCurrencies(market);
    }

    public boolean shouldLoadTradesFromServer(Markets market, Currency currency) {
        Boolean shouldLoadTrades = shouldLoadTradesMap.get(new MultiKey(market, currency));
        if (shouldLoadTrades == null) {
            LOG.info("There is no trades information for market: " + HistoryDownloader.getMarketIdentifierName(market, currency));
            return false;
        }
        return shouldLoadTrades;
    }

    @Override
    public Set<TradesFullLayoutObject> getTrades(Markets market, Currency currency, Long timestamp, boolean initialLoad) {
        List<TradesFullLayoutObject> result;
        if (initialLoad) {  //in case the client doesn't have any trades yet, give him all the trades (the set has a maximum size
            result = getAllTrades(market, currency);
        } else {    //in case the client already has the initial trades, he loads just what what he doesn't yet have
            result = getTrades(market, currency, timestamp);
        }

        //Collections.reverse(result);
        return new LinkedHashSet<>(result);
    }

    class TradesEngineRunnable implements Runnable {

        private Markets market;
        private Currency currency;

        TradesEngineRunnable(Markets market, Currency currency) {
            this.market = market;
            this.currency = currency;
        }

        @Override
        public void run() {
            loadAndSaveTradesPerMarketAndCurrency(market, currency);
        }
    }

    @Override
    public void shutdown() {
        LOG.info("Shutting down the Generic Trades Engine");
        try {
            //closing down the trades refresher
            //executor.shutdownNow();
        } catch (Exception e) {
            LOG.error("Error occurred while shutting down the trades refresher", e);
        }

        try {
            //shutting down the mongo DB connection
            dao.shutdown();
        } catch (Exception e) {
            LOG.error("Error occurred while shutting down the DB connection", e);
        }
    }
}
