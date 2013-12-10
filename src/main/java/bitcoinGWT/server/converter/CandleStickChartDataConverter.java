package bitcoinGWT.server.converter;

import bitcoinGWT.shared.model.ChartElement;
import bitcoinGWT.shared.model.TimeInterval;
import bitcoinGWT.shared.model.TimeWindow;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.carrotsearch.sizeof.RamUsageEstimator;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/27/13
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CandleStickChartDataConverter {

    public static Set<ChartElement> get10MinutesChartElements(Set<TradesFullLayoutObject> tradesResult) {
        return convert(tradesResult, TimeInterval.TEN_MINUTES);
    }

    public static Set<ChartElement> get1HourChartElements(Set<TradesFullLayoutObject> tradesResult) {
        return convert(tradesResult, TimeInterval.ONE_HOUR);
    }

    private static Set<ChartElement> convert(Set<TradesFullLayoutObject> tradesResult, TimeInterval timeInterval) {
        Set<ChartElement> result = new LinkedHashSet<>();
        System.out.println(new Date() + ": converting " + tradesResult.size() + " trade items");

        Map<TimeWindow, LinkedList<TradesFullLayoutObject>> partialResults = new LinkedHashMap<>();
        getPartialResults(partialResults, new ArrayDeque<>(tradesResult), timeInterval.getMinutes());
        System.out.println(new Date() + ": size of partial results=" + RamUsageEstimator.humanSizeOf(partialResults));

        for (Map.Entry<TimeWindow, LinkedList<TradesFullLayoutObject>> entry : partialResults.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                double open, close, amount = 0; //initialize the values of the chart element
                double low = Double.POSITIVE_INFINITY;
                double high = Double.NEGATIVE_INFINITY;
                //open value is the price of the first element
                open = entry.getValue().getFirst().getPrice();

                //close value is the price of the last element
                close = entry.getValue().getLast().getPrice();

                //time of the last trade
                Date timeOfLastTrade = new Date(0);

                //find out Max/Min values
                for (TradesFullLayoutObject trade : entry.getValue()) {
                    //low is the lowest value from the interval (originally +infinity)
                    low = Math.min(low, trade.getPrice());
                    //high is the lowest value from the interval (originally -infinity)
                    high = Math.max(high, trade.getPrice());

                    //add the current trade amount to the total trade count
                    amount += trade.getAmount();

                    timeOfLastTrade = new Date(Math.max(timeOfLastTrade.getTime(), trade.getDate().getTime()));
                }

                //now we have all values, create the chart element
                ChartElement element = new ChartElement(open, close, low, high, amount, entry.getKey(), timeOfLastTrade, timeInterval);
                result.add(element);
            }

        }
        System.out.println(new Date() + ": converted " + tradesResult.size() + " trade items into " + result.size() + " chart elements");
        return result;
    }

    private static Map<TimeWindow, LinkedList<TradesFullLayoutObject>> getPartialResults(
            Map<TimeWindow, LinkedList<TradesFullLayoutObject>> result,
            ArrayDeque<TradesFullLayoutObject> queue,
            int timeAmount) {
        //System.out.println(new Date() + ": running getPartialResults");
        //System.out.println(new Date() + ": queue size: " + RamUsageEstimator.humanSizeOf(queue));
        //take the first element out of the queue
        TradesFullLayoutObject firstElement = queue.pollLast();

        //if the queue is not empty
        if (firstElement != null) {
            Calendar cal = GregorianCalendar.getInstance();
            //initialize the calendar with the trade date
            cal.setTime(firstElement.getDate());

            //add timeAmount minutes
            cal.add(Calendar.MINUTE, timeAmount);

            //create the corresponding time window for the current element
            TimeWindow window = new TimeWindow(firstElement.getDate(), cal.getTime());

            //try to find a time window corresponding to the current element
            TimeWindow matchingTimeWindow = getCorrespondingWindow(result.keySet(), firstElement);

            //if we have a time window in which the current element fits, add it the trade item list of that time window
            if (matchingTimeWindow != null) {
                LinkedList<TradesFullLayoutObject> matchingTrades = result.get(matchingTimeWindow);
                matchingTrades.add(firstElement);
            } else {    //in case there is no time window corresponding to the current element, add a new entry in the result
                LinkedList<TradesFullLayoutObject> matchingTrades = new LinkedList<>();
                matchingTrades.add(firstElement);
                result.put(window, matchingTrades);
            }

            //go over all the rest of the trade items, find partial results
            //System.out.println(new Date() + ": size of tempQueue=" + RamUsageEstimator.humanSizeOf(queue));
            getPartialResults(result, queue, timeAmount);

            //when getting here, the queue is empty, but all trade items are still to be split into time windows

            //in any case, the result will either contain:
            // - single element with the current time window and current trade item
            // - an already existing list of elements, + a new element (current time window, current trade item)
            return result;
        } else {    //reached queue end, return empty result
            return new LinkedHashMap<>();
        }
    }

    private static TimeWindow getCorrespondingWindow(Collection<TimeWindow> partialResults, TradesFullLayoutObject tradeItem) {
        if (partialResults == null || partialResults.isEmpty()) {
            return null;
        }

        for (TimeWindow window : partialResults) {
            //if the trade item is inside the time window
            if ((tradeItem.getDate().getTime() == window.getStart().getTime()
                        || tradeItem.getDate().after(window.getStart()))
                    && (tradeItem.getDate().getTime() == window.getEnd().getTime()
                        || tradeItem.getDate().before(window.getEnd()))) {
                return window;
            }
        }

        //the trade item is not within any time windows
        return null;
    }
}
