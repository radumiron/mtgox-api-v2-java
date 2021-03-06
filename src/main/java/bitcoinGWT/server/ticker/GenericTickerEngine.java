package bitcoinGWT.server.ticker;

import bitcoinGWT.shared.model.*;
import bitcoinGWT.server.history.HistoryDownloader;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import trading.api_interfaces.TradeInterface;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/15/14
 * Time: 11:51 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Primary
@Qualifier("GENERIC")
public class GenericTickerEngine extends TickerEngine {

    private static final Logger LOG = Logger.getLogger(GenericTickerEngine.class);

    @Autowired
    @Qualifier("GENERIC")
    private GenericTradesEngine tradesEngine;

    @Autowired
    @Qualifier("XChange")
    private TradeInterface trade;

    private ExecutorService executor;

    private ConcurrentHashMap<MultiKey, TickerFullLayoutObject> tickerMap;

    @PostConstruct
    private void init() {
        LOG.info("init of GenericTickerEngine");
        //executor = Executors.newFixedThreadPool(Currency.values().length);
        executor = Executors.newSingleThreadExecutor();
        tickerMap = new ConcurrentHashMap<>();
    }

    @Override
    protected void executeTradeTask() {
        getTicker();
    }

    @Override
    protected int getTimerInterval() {
        return Constants.TICKER_INTERVAL;
    }


    protected String getTradeName() {
        return this.getClass().getName();
    }

    @Override
    public void shutdown() {
        LOG.info("Shutting down the Generic Ticker Engine");
        try {
            //closing down the trades refresher
            executor.shutdownNow();
        } catch (Exception e) {
            LOG.error("Error occurred while shutting down the trades refresher", e);
        }
    }

    public TickerFullLayoutObject getPrice(Markets market, Currency currency) {
        return tickerMap.get(new MultiKey(market, currency));
    }

    private void getTicker() {
        //go over all supported markets
        for (Markets market : Markets.values()) {
            getTickerPerMarket(market);
        }

    }

    private void getTickerPerMarket(Markets market) {
        Set<Currency> supportedCurrencies = trade.getSupportedCurrencies(market);

        //go over all supported currencies for the current market
        for (Currency currency : supportedCurrencies) {
            executor.submit(new TickerEngineRunnable(market, currency));
        }
    }

    private void getTickerPerMarketAndCurrency(Markets market, Currency currency) {
        String marketIdentifier = HistoryDownloader.getMarketIdentifierName(market, currency);
        TickerShallowObject tradeObject = trade.getPrice(market, currency);
        if (tradeObject instanceof TickerFullLayoutObject) {
            //if all went well, we should have a full layout object
            TickerFullLayoutObject currentTicker = (TickerFullLayoutObject) tradeObject;

            //get old value from the cache
            TickerFullLayoutObject oldTicker = tickerMap.get(new MultiKey(market, currency));
            if (oldTicker != null) {    //if we already have a ticker
                //check if there is a difference between the tickers, and load the trades only then
                //the difference between the tickers is most probably caused by some trades
                if (oldTicker.getPrice() != currentTicker.getPrice()) {
                    LOG.info("Ticker value changed (oldTicker=" + oldTicker.getPrice()
                            + ", newTicker=" + currentTicker.getPrice() + "), have to load trades for market:" + marketIdentifier);
                    tradesEngine.loadAndSaveTradesPerMarketAndCurrency(market, currency);
                }
            } else {    //in case the old ticker is null, then it's the first time we load the ticker, hence also load the trades
                tradesEngine.loadAndSaveTradesPerMarketAndCurrency(market, currency);
            }

            //put this object in the cache
            tickerMap.put(new MultiKey(market, currency), currentTicker);

            double price = currentTicker.getPrice();
            //String lag = trade.getLag();
            LOG.info("Last price=" + price + " for:" + marketIdentifier);// + ", lag: " + lag);
        } else {
            LOG.info("Something went wrong when getting the price. Got a shallow object instead of a full object");
            //String lag = trade.getLag();
            LOG.info("Last price=" + tradeObject.getPrice() + " for:" + marketIdentifier);// + ", lag: " + lag);
        }
        LOG.info("After each ticker task, " + HistoryDownloader.getMarketIdentifierName(market, currency));
    }

    class TickerEngineRunnable implements Runnable {

        private Markets market;
        private Currency currency;

        TickerEngineRunnable(Markets market, Currency currency) {
            this.market = market;
            this.currency = currency;
        }

        @Override
        public void run() {
            Date before = new Date();
            LOG.info("Running ticker thread for:" + HistoryDownloader.getMarketIdentifierName(market, currency));
            getTickerPerMarketAndCurrency(market, currency);
            LOG.info("After running ticker thread for:" + HistoryDownloader.getMarketIdentifierName(market, currency)
                    + ", operation took:" + (new Date().getTime() - before.getTime()) + " ms");
        }
    }
}
