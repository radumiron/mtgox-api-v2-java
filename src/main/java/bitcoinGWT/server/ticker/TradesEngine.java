package bitcoinGWT.server.ticker;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.carrotsearch.sizeof.RamUsageEstimator;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/25/13
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TradesEngine extends AbstractTradeEngine {

    //TODO increase this to 7 days (1 week)
    private static final int INITIAL_TRADES_INTERVAL = /*12 * 60*/30 * 60 * 1000; //last 12 hours

    private static final int TRADES_RETRIEVAL_INTERVAL = 5000;//60000;

    private static final int TRADES_SIZE = 5000;

    private Long previousTimestamp;

    private List<TradesFullLayoutObject> lastLoadedTrades = new ArrayList<>();

    private LinkedHashMap<Date, List<TradesFullLayoutObject>> allTrades;

    private boolean shouldLoadTrades = false;

    public TradesEngine() {
        allTrades = new LinkedHashMap<Date, List<TradesFullLayoutObject>>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Date, List<TradesFullLayoutObject>> eldest) {
                return size() > TRADES_SIZE;
            }
        };
    }

    @Override
    protected void executeTradeTask() {
        lastLoadedTrades = trade.getTrades(Currency.EUR, getPreviousTimestamp());
        for (TradesFullLayoutObject trade : lastLoadedTrades) {
            System.out.println("trade size=" + RamUsageEstimator.humanSizeOf(trade) + "," + trade);
            //add each trade to the map. Because the map holds only the last TRADES_SIZE, this method will also remove the oldest entries.
            List<TradesFullLayoutObject> tradesAtDate;
            //in case we already have some trades at some trade specific trade second, add the new trade there
            if ((tradesAtDate = allTrades.get(trade.getDate())) != null) {
                tradesAtDate.add(trade);
                //System.out.println("trades list=" + RamUsageEstimator.humanSizeOf(tradesAtDate));
                //System.out.println("trades before adding 1 trade:" + RamUsageEstimator.humanSizeOf(allTrades));
                allTrades.put(trade.getDate(), tradesAtDate);
                //System.out.println("trades after adding 1 trade:" + RamUsageEstimator.humanSizeOf(allTrades));
            } else {    //if there aren't yet any trades at a specific date, add the current trade as the first one
                tradesAtDate = new ArrayList<>();
                tradesAtDate.add(trade);
                //System.out.println("trades list=" + RamUsageEstimator.humanSizeOf(tradesAtDate));
                //System.out.println("trades before adding 1 trade:" + RamUsageEstimator.humanSizeOf(allTrades));
                allTrades.put(trade.getDate(), tradesAtDate);
                //System.out.println("trades after adding 1 trade:" + RamUsageEstimator.humanSizeOf(allTrades));
            }
        }

        if (lastLoadedTrades.size() > 0) {
            System.out.println(new Date() + " size of loaded trades=" + lastLoadedTrades.size());
            //in case the list downloaded is NOT empty, mark that all who will ask for trades will be able to download the new list
            //shouldLoadTrades = true;
            //System.out.println(new Date() + " before adding new trades, size of allTrades=" + RamUsageEstimator.humanSizeOf(allTrades));
        } else {
            //in case the list is empty,
            //shouldLoadTrades = false
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

    public Set<TradesFullLayoutObject> getTrades(Currency currency, int sizeOfLoadedTrades) {
        if (sizeOfLoadedTrades == 0) {  //in case the client doesn't have any trades yet, give him all the trades
            return getAllTrades();
        } else {    //in case the client already has the initial trades, he loads just what what he doesn't yet have
            LinkedHashSet<TradesFullLayoutObject> lastTradesSet = new LinkedHashSet<>(lastLoadedTrades);
            return lastTradesSet;
        }
    }

    private Set<TradesFullLayoutObject> getAllTrades() {
        LinkedHashSet<TradesFullLayoutObject> result = new LinkedHashSet<>();
        //first copy the map
        LinkedHashMap<Date, List<TradesFullLayoutObject>> allTradesCopy = new LinkedHashMap<>(allTrades);
        for (Map.Entry<Date, List<TradesFullLayoutObject>> entry : allTradesCopy.entrySet()) {
            //add all values inside the list corresponding to each trading second to the result
            result.addAll(entry.getValue());
        }

        return result;
    }
}
