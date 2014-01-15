package bitcoinGWT.server.ticker;

import bitcoinGWT.server.util.ObjectSizeCalculator;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.carrotsearch.sizeof.RamUsageEstimator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import trading.api_interfaces.MtGoxTradeInterface;
import trading.api_interfaces.TradeInterface;

import java.util.*;

import static bitcoinGWT.shared.model.Constants.*;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 6/25/13
 * Time: 9:34 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Qualifier("MTGOX_EUR")
public class TradesEngine extends AbstractTradeEngine {

    @Autowired
    protected MtGoxTradeInterface trade;

    private Long previousTimestamp;

    private LinkedHashMap<Long, TradesFullLayoutObject> allLoadedTrades;

    private long largestTradeSize = Integer.MIN_VALUE;

    private boolean shouldLoadTrades;

    public TradesEngine() {
        allLoadedTrades = new LinkedHashMap<Long, TradesFullLayoutObject>() {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, TradesFullLayoutObject> eldest) {
                //todo think of a solution to remove trades based on trade date, not map size
                return size() > TRADES_SIZE;
            }
        };
    }

    @Override
    protected void executeTradeTask() {/*
        List<TradesFullLayoutObject> sortedTrades = new ArrayList<>(trade.getTrades(Currency.EUR, getPreviousTimestamp()));

        if (sortedTrades.size() > 0) {
            //put the current trades at the current timestamp

            for (TradesFullLayoutObject trade : sortedTrades) {
                long tradeSize = RamUsageEstimator.sizeOf(trade);
                if (tradeSize > largestTradeSize) {
                    largestTradeSize = tradeSize;
                }
                System.out.println("trade size=" + RamUsageEstimator.humanReadableUnits(tradeSize) + "," + trade);
                //add each trade to the map. Because the map holds only the last TRADES_SIZE, this method will also remove the oldest entries.
                allLoadedTrades.put(trade.getTradeId(), trade);
            }

            System.out.println(new Date() + " new trades loaded, size of loaded trades=" + sortedTrades.size());
            //in case the list downloaded is NOT empty, mark that all who will ask for trades will be able to download the new list
            shouldLoadTrades = true;
        } else {
            //in case the list is empty,
            shouldLoadTrades = false;
        }
        System.out.println(new Date() + " total size of cached trades=" + allLoadedTrades.size() + ", memoryConsumption=" + RamUsageEstimator.humanReadableUnits(allLoadedTrades.size() * largestTradeSize));
        System.out.println(new Date() + " total size of cached trades=" + allLoadedTrades.size() + ", memoryConsumption=" + RamUsageEstimator.humanReadableUnits(RamUsageEstimator.sizeOfAll(allLoadedTrades)));
        System.out.println(new Date() + " total size of cached trades=" + allLoadedTrades.size() + ", memoryConsumption=" + RamUsageEstimator.humanReadableUnits(ObjectSizeCalculator.getObjectSize(allLoadedTrades)));
        System.out.println(new Date() + " total size of cached trades=" + allLoadedTrades.size() + ", memoryConsumption=" + RamUsageEstimator.humanReadableUnits(RamUsageEstimator.sizeOfAll(allLoadedTrades.values())));
        System.out.println();
        */
    }

   /* private void addTrade(TradesFullLayoutObject trade) {
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
    }*/

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

    private Set<TradesFullLayoutObject> getAllTrades() {
        LinkedHashSet<TradesFullLayoutObject> result = new LinkedHashSet<>();
        //first copy the map
        List<TradesFullLayoutObject> allTradesCopy = new ArrayList<>(allLoadedTrades.values());
        //reverse the result list in order to have it "oldest first"
        Collections.reverse(allTradesCopy);
        result.addAll(allTradesCopy);
        return result;
    }

    public Set<TradesFullLayoutObject> getTrades(Markets markets, Currency currency, Long timestamp, boolean initialLoad) {
        if (initialLoad) {  //in case the client doesn't have any trades yet, give him all the trades
            return getAllTrades();
        } else {    //in case the client already has the initial trades, he loads just what what he doesn't yet have
            List<TradesFullLayoutObject> lastTradesList = new ArrayList<>();

            Date clientDate;
            if (timestamp == null) {
                System.out.println(new Date() + ": WARNING, the client wants to retrieve trades without supplying a timestamp");
                clientDate = new Date();
            } else {
                clientDate = new Date(timestamp);   //initialize the date coming from the customer
            }

            //first, make a copy of the allTrades
            Map<Long, TradesFullLayoutObject> copyOfAllLoadedTrades = new LinkedHashMap<>(allLoadedTrades);
            //todo find a better way to retrieve chronological data
            for (Map.Entry<Long, TradesFullLayoutObject> entry : copyOfAllLoadedTrades.entrySet()) {
                Date tradeDate = entry.getValue().getDate();
                if (tradeDate.after(clientDate)) { //if the trade is after what the customer has
                    lastTradesList.add(entry.getValue());
                }
            }

            //reverse the trades diff
            Collections.reverse(lastTradesList);
            return new LinkedHashSet<>(lastTradesList);
        }
    }

    public boolean shouldLoadTradesFromServer(Markets market, Currency currency) {
        return shouldLoadTrades;
    }

    public List<Currency> getSupportedCurrencies(Markets market) {
        return trade.getSupportedCurrencies(market);
    }
}
