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

    private static final int INITIAL_TRADES_INTERVAL = 12 * 60 * 60 * 1000; //last 12 hours

    private static final int TRADES_RETRIEVAL_INTERVAL = 5000;//60000;

    private Long previousTimestamp;

    private Set<TradesFullLayoutObject> allTrades = new LinkedHashSet<>();

    @Override
    protected void executeTradeTask() {
        List<TradesFullLayoutObject> trades = trade.getTrades(Currency.EUR, getPreviousTimestamp());
        for (TradesFullLayoutObject trade : trades) {
            System.out.println(trade);
        }

        if (trades.size() > 0) {
            System.out.println(new Date() + " before adding new trades, size of allTrades=" + RamUsageEstimator.humanSizeOf(allTrades));
        }
        allTrades.addAll(trades);
        System.out.println(new Date() + " size of allTrades=" + RamUsageEstimator.humanSizeOf(allTrades));
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
}
