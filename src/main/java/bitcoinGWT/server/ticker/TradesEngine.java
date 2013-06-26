package bitcoinGWT.server.ticker;

import bitcoinGWT.shared.model.TradesFullLayoutObject;
import mtgox_api.com.mtgox.api.MtGox;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

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

    @Override
    protected void executeTradeTask() {
        List<TradesFullLayoutObject> trades = trade.getTrades(MtGox.Currency.EUR, getPreviousTimestamp());
        for (TradesFullLayoutObject trade : trades) {
            System.out.println(trade);
        }
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
