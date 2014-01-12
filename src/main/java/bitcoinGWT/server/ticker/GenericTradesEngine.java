package bitcoinGWT.server.ticker;

import bitcoinGWT.server.converter.TradesConverter;
import bitcoinGWT.server.dao.GenericDAO;
import bitcoinGWT.server.dao.entities.TradesFullLayoutRecord;
import bitcoinGWT.server.dao.entities.TradesHistoryRecord;
import bitcoinGWT.server.util.ObjectSizeCalculator;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.carrotsearch.sizeof.RamUsageEstimator;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import history.HistoryDownloader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import trading.api_interfaces.TradeInterface;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    @Autowired
    @Qualifier("MONGO")
    private GenericDAO dao;

    @Autowired
    private TradeInterface trade;

    private ExecutorService executor;

    private Table<Markets, Currency, Boolean> shouldLoadTradesMap;

    private Long previousTimestamp;

    @PostConstruct
    private void init() {
        executor = Executors.newFixedThreadPool(20);
        shouldLoadTradesMap = HashBasedTable.create();
    }

    @Override
    protected void executeTradeTask() {
        loadAndSaveTrades();
    }

    private void loadAndSaveTrades() {
        //go over all supported markets
        for (Markets market : Markets.values()) {
            loadAndSaveTradesPerMarket(market);
        }
    }

    private void loadAndSaveTradesPerMarket(Markets market) {
        List<Currency> supportedCurrencies = trade.getSupportedCurrencies(market);

        //go over all supported currencies for the current market
        //todo currently we load just for EUR
        executor.execute(new TradesEngineRunnable(market, Currency.EUR));
        /*for (Currency currency : supportedCurrencies) {
            executor.execute(new TradesEngineRunnable(market, currency));
        }*/
    }

    private void loadAndSaveTradesPerMarketAndCurrency(Markets market, Currency currency) {
        //todo in case previousTimestamp is more recent than the latest record in the DB, save new trades
        List<TradesFullLayoutObject> sortedTrades = new ArrayList<>(trade.getTrades(market, currency, getPreviousTimestamp()));

        if (sortedTrades.size() > 0) {
            //save the new trades in the database
            Map<String, List<TradesFullLayoutRecord>> recordsToSave = new HashMap<>();
            recordsToSave.put(HistoryDownloader.getMarketIdentifierName(market, currency),
                    TradesConverter.convertTradesFullLayoutObjectsToTradesFullLayoutRecords(sortedTrades));
            dao.saveTradesFullLayoutRecords(recordsToSave, true);

            //save also data for the history (for the chart)
            Map<String, List<TradesHistoryRecord>> historyRecordsToSave = new HashMap<>();
            historyRecordsToSave.put(HistoryDownloader.getMarketIdentifierName(market, currency),
                    TradesConverter.convertTradesShallowObjectsToTradesHistoryRecords(sortedTrades));
            dao.saveTradesHistoryRecords(historyRecordsToSave, true);

            System.out.println(new Date() + " new trades loaded, size of loaded trades=" + sortedTrades.size());
            //in case the list downloaded is NOT empty, mark that all who will ask for trades will be able to download the new list
            shouldLoadTradesMap.put(market, currency, true);
        } else {
            //in case the list is empty,
            shouldLoadTradesMap.put(market, currency, false);
        }
    }

    public boolean shouldLoadTradesFromServer(Markets market, Currency currency) {
        return shouldLoadTradesMap.get(market, currency);
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

    private List<TradesFullLayoutObject> getAllTrades(Markets market, Currency currency) {
        return getTrades(market, currency, null);
    }

    private List<TradesFullLayoutObject> getTrades(Markets market, Currency currency, Long timestamp) {
        List<TradesFullLayoutRecord> records = dao.getTradesFullLayoutRecords(HistoryDownloader.getMarketIdentifierName(market, currency), timestamp, true);
        return TradesConverter.convertTradesFullLayoutRecordsToTradesFullLayoutObjects(records);
    }

    private long getPreviousTimestamp() {
        Calendar calendar = GregorianCalendar.getInstance();
        //set the time when this method was previously executed
        if (previousTimestamp == null) {    //if first download of trades
            System.out.println("first time trades");
            previousTimestamp = calendar.getTimeInMillis();
            System.out.println("current time: " + new Date(previousTimestamp));
            //go back the specified amount of seconds
            calendar.add(Calendar.MILLISECOND, (-1) * INITIAL_TRADES_INTERVAL);
            System.out.println("getting trades since: " + calendar.getTime());
        } else {
            long temporaryTimestamp = calendar.getTimeInMillis();   //save the current execution time to a temp variable
            //roll back the calendar with the amount of milliseconds passed since the last run.
            calendar.add(Calendar.MILLISECOND, (-1) * (int) (calendar.getTimeInMillis() - previousTimestamp));

            System.out.println("getting trades from: " + new Date(previousTimestamp) + " to " + new Date(temporaryTimestamp));
            //save the current run time as a reference for the next run time.
            previousTimestamp = temporaryTimestamp;
        }

        //return the actual time since when to return the trades.
        return calendar.getTimeInMillis();
    }

    @Override
    protected int getTimerInterval() {
        return TRADES_RETRIEVAL_INTERVAL;
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
}
