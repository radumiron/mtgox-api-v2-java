package bitcoinGWT.server.ticker;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.carrotsearch.sizeof.RamUsageEstimator;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

import static bitcoinGWT.shared.model.Constants.*;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/25/13
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TradesEngine extends AbstractTradeEngine {

    private Long previousTimestamp;

    private LinkedBlockingQueue<TradesFullLayoutObject> lastLoadedTrades = new LinkedBlockingQueue<>();

    private int loadedTradesListSize;

    private LinkedHashMap<Long, TradesFullLayoutObject> allTrades;
    private boolean shouldLoadTrades;

    public TradesEngine() {
        allTrades = new LinkedHashMap<Long, TradesFullLayoutObject>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, TradesFullLayoutObject> eldest) {
                return /*(size() > TRADES_SIZE) || */(eldest.getValue().getDate().before(getOldestTradeDate()));
            }
        };
    }

    @Override
    protected void executeTradeTask() {
        lastLoadedTrades = new LinkedBlockingQueue<>(trade.getTrades(Currency.EUR, getPreviousTimestamp()));
        for (TradesFullLayoutObject trade : lastLoadedTrades) {
            System.out.println("trade size=" + RamUsageEstimator.humanSizeOf(trade) + "," + trade);
            //add each trade to the map. Because the map holds only the last TRADES_SIZE, this method will also remove the oldest entries.
            allTrades.put(trade.getTradeId(), trade);
        }

        if (lastLoadedTrades.size() > 0) {
            System.out.println(new Date() + " new trades loaded, size of loaded trades=" + lastLoadedTrades.size());
            loadedTradesListSize = lastLoadedTrades.size();
            //in case the list downloaded is NOT empty, mark that all who will ask for trades will be able to download the new list
            shouldLoadTrades = true;
            //System.out.println(new Date() + " before adding new trades, size of allTrades=" + RamUsageEstimator.humanSizeOf(allTrades));
        } else {
            //in case the list is empty,
            shouldLoadTrades = false;
        }
        System.out.println(new Date() + " total size of cached trades=" + allTrades.size());
        //allTrades.addAll(trades);
        //System.out.println(new Date() + " size of allTrades=" + RamUsageEstimator.humanSizeOf(allTrades));
        System.out.println();
    }

    @Override
    protected int getTimerInterval() {
        return TRADES_RETRIEVAL_INTERVAL;
    }

    private long getPreviousTimestamp() {
        Calendar calendar = GregorianCalendar.getInstance();
        //set the time when this method was previously executed
        if (previousTimestamp == null) {    //if first download of trades
            previousTimestamp = calendar.getTimeInMillis();
            //go back the specified amount of seconds
            calendar.add(Calendar.MILLISECOND, (-1) * INITIAL_TRADES_INTERVAL);
        } else {
            long temporaryTimestamp = calendar.getTimeInMillis();   //save the current execution time to a temp variable
            //roll back the calendar with the amount of milliseconds passed since the last run.
            calendar.add(Calendar.MILLISECOND, (-1) * (int) (calendar.getTimeInMillis() - previousTimestamp));

            //save the current run time as a reference for the next run time.
            previousTimestamp = temporaryTimestamp;
        }

        //return the actual time since when to return the trades.
        return calendar.getTimeInMillis();
    }

    public Set<TradesFullLayoutObject> getTrades(Currency currency, boolean initialLoad) {
        if (initialLoad) {  //in case the client doesn't have any trades yet, give him all the trades
            return getAllTrades();
        } else {    //in case the client already has the initial trades, he loads just what what he doesn't yet have
            LinkedHashSet<TradesFullLayoutObject> lastTradesSet = new LinkedHashSet<>(lastLoadedTrades);
            return lastTradesSet;
        }
    }

    public boolean shouldLoadTradesFromServer(Currency currency) {
        return shouldLoadTrades;
    }

    private Set<TradesFullLayoutObject> getAllTrades() {
        LinkedHashSet<TradesFullLayoutObject> result = new LinkedHashSet<>();
        //first copy the map
        LinkedHashMap<Long, TradesFullLayoutObject> allTradesCopy = new LinkedHashMap<>(allTrades);
        result.addAll(allTradesCopy.values());
        return result;
    }

    private Date getOldestTradeDate() {
        //return new Date(System.currentTimeMillis() - INITIAL_TRADES_INTERVAL);
        return new Date(System.currentTimeMillis() - 1 *  60 * 60 * 1000);
        /*Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -6);
        Date date = calendar.getTime();*/
    }
}
