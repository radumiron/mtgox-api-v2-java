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
    private LinkedBlockingQueue<TradesFullLayoutObject> allLoadedTrades = new LinkedBlockingQueue<>(TRADES_SIZE);
    private LinkedBlockingQueue<TradesFullLayoutObject> copyOfLastLoadedTrades = new LinkedBlockingQueue<>();

    private int loadedTradesListSize;

    private boolean shouldLoadTrades;

    @Override
    protected void executeTradeTask() {
        List<TradesFullLayoutObject> sortedTrades = new ArrayList<>(trade.getTrades(Currency.EUR, getPreviousTimestamp()));
        Collections.sort(sortedTrades, new Comparator<TradesFullLayoutObject>() {
            @Override
            public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                //return the trades in a decreasing order - newest first.
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        lastLoadedTrades.clear();
        lastLoadedTrades.addAll(sortedTrades);

        for (TradesFullLayoutObject trade : lastLoadedTrades) {
            System.out.println("trade size=" + RamUsageEstimator.humanSizeOf(trade) + "," + trade);
            //add each trade to the map. Because the map holds only the last TRADES_SIZE, this method will also remove the oldest entries.
            addTrade(trade);
        }

        if (lastLoadedTrades.size() > 0) {
            //add the new elements to the copy
            copyOfLastLoadedTrades.addAll(lastLoadedTrades);

            System.out.println(new Date() + " new trades loaded, size of loaded trades=" + lastLoadedTrades.size());
            loadedTradesListSize = lastLoadedTrades.size();
            //in case the list downloaded is NOT empty, mark that all who will ask for trades will be able to download the new list
            shouldLoadTrades = true;
        } else {
            //in case the list is empty,
            shouldLoadTrades = false;
        }
        System.out.println(new Date() + " total size of cached trades=" + allLoadedTrades.size());
        System.out.println();
    }

    private void addTrade(TradesFullLayoutObject trade) {
        if (allLoadedTrades.isEmpty()) {
            allLoadedTrades.add(trade);
        } else {
            if (allLoadedTrades.remainingCapacity() > 0) {
                allLoadedTrades.offer(trade);
            } else {
                allLoadedTrades.poll();
                allLoadedTrades.offer(trade);
            }
        }
    }

    @Override
    protected int getTimerInterval() {
        return TRADES_RETRIEVAL_INTERVAL;
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

    public Set<TradesFullLayoutObject> getTrades(Currency currency, boolean initialLoad, boolean blockingCall) {
        if (initialLoad) {  //in case the client doesn't have any trades yet, give him all the trades
            return getAllTrades();
        } else {    //in case the client already has the initial trades, he loads just what what he doesn't yet have
            if (blockingCall) {
                //create a copy

                LinkedHashSet<TradesFullLayoutObject> lastTradesSet = new LinkedHashSet<>();
                //while the copy is not empty, parse it
                do {
                    try {
                        //try to take one element at a time; if there are elements, they will be added to the result set
                        //if there aren't any elements,
                        lastTradesSet.add(copyOfLastLoadedTrades.take());
                    } catch (InterruptedException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                } while (!copyOfLastLoadedTrades.isEmpty());

                return lastTradesSet;

            }
            LinkedHashSet<TradesFullLayoutObject> lastTradesSet = new LinkedHashSet<>(lastLoadedTrades);
            return lastTradesSet;
        }
    }

    public Set<TradesFullLayoutObject> getTrades(Currency currency, boolean initialLoad) {
        return getTrades(currency, initialLoad, false);
    }

    public boolean shouldLoadTradesFromServer(Currency currency) {
        return shouldLoadTrades;
    }

    private Set<TradesFullLayoutObject> getAllTrades() {
        LinkedHashSet<TradesFullLayoutObject> result = new LinkedHashSet<>();
        //first copy the map
        List<TradesFullLayoutObject> allTradesCopy = new ArrayList<>(allLoadedTrades);
        //reverse the result list in order to have it "oldest first"
        Collections.reverse(allTradesCopy);
        result.addAll(allLoadedTrades);
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
