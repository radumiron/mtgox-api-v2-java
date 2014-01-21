package trading.bitcoinXChange;

import bitcoinGWT.shared.model.*;
import bitcoinGWT.shared.model.Currency;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsExchange;
import com.xeiam.xchange.bitcurex.BitcurexExchange;
import com.xeiam.xchange.bitstamp.BitstampExchange;
import com.xeiam.xchange.blockchain.BlockchainExchange;
import com.xeiam.xchange.btcchina.BTCChinaExchange;
import com.xeiam.xchange.btce.BTCEExchange;
import com.xeiam.xchange.campbx.CampBXExchange;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.kraken.KrakenExchange;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import com.xeiam.xchange.mtgox.v2.MtGoxV2;
import com.xeiam.xchange.mtgox.v2.dto.trade.polling.MtGoxLagWrapper;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import com.xeiam.xchange.virtex.VirtExExchange;
import bitcoinGWT.server.history.HistoryDownloader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import si.mazi.rescu.RestProxyFactory;
import trading.api_interfaces.TradeInterface;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/9/14
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Primary
@Qualifier("XChange")
public class XChangeTrading implements TradeInterface {

    private Map<Markets, PollingMarketDataService> marketsServiceMap;
    private Map<Markets, Exchange> marketsExchangeMap;

    @PostConstruct
    private void initTradingObject() {
        //create the markets to service map
        marketsServiceMap = new HashMap<>();
        marketsExchangeMap = new HashMap<>();
        initServices();
    }

    private void initServices() {
        // Use the factory to get the version 2 MtGox exchange API using default settings
        Exchange bitcoinChartsExchange = ExchangeFactory.INSTANCE.createExchange(BitcoinChartsExchange.class.getName());
        Exchange bitcurexExchange = ExchangeFactory.INSTANCE.createExchange(BitcurexExchange.class.getName());
        Exchange bitstampExchange = ExchangeFactory.INSTANCE.createExchange(BitstampExchange.class.getName());
        Exchange blockchainExchange = ExchangeFactory.INSTANCE.createExchange(BlockchainExchange.class.getName());
        Exchange btcchinaExchange = ExchangeFactory.INSTANCE.createExchange(BTCChinaExchange.class.getName());
        Exchange btceExchange = ExchangeFactory.INSTANCE.createExchange(BTCEExchange.class.getName());
        Exchange campBxExchange = ExchangeFactory.INSTANCE.createExchange(CampBXExchange.class.getName());
        Exchange cavirtexExchange = ExchangeFactory.INSTANCE.createExchange(VirtExExchange.class.getName());
        Exchange krakenExchange = ExchangeFactory.INSTANCE.createExchange(KrakenExchange.class.getName());
        Exchange mtGoxExchange = ExchangeFactory.INSTANCE.createExchange(MtGoxExchange.class.getName());

        /*marketsExchangeMap.put(Markets.BITCOINCHARTS, bitcoinChartsExchange);
        marketsExchangeMap.put(Markets.BITCUREX, bitcurexExchange);
        marketsExchangeMap.put(Markets.BITSTAMP, bitstampExchange);
        marketsExchangeMap.put(Markets.BTCCHINA, btcchinaExchange);*/
        marketsExchangeMap.put(Markets.BTCE, btceExchange);
        /*marketsExchangeMap.put(Markets.CAMPBX, campBxExchange);
        //marketsExchangeMap.put(Markets.CAVIRTEX, cavirtexExchange);
        marketsExchangeMap.put(Markets.KRAKEN, krakenExchange);*/
        marketsExchangeMap.put(Markets.MTGOX, mtGoxExchange);

        // Interested in the public polling market data feed (no authentication)

        PollingMarketDataService bitcurexService = bitcurexExchange.getPollingMarketDataService();
        PollingMarketDataService bitstampService = bitstampExchange.getPollingMarketDataService();
        PollingMarketDataService blockchainService = blockchainExchange.getPollingMarketDataService();
        PollingMarketDataService btcchinaService = btcchinaExchange.getPollingMarketDataService();
        PollingMarketDataService btceService = btceExchange.getPollingMarketDataService();
        PollingMarketDataService campBxService = campBxExchange.getPollingMarketDataService();
        PollingMarketDataService cavirtexService = cavirtexExchange.getPollingMarketDataService();
        PollingMarketDataService krakenService = krakenExchange.getPollingMarketDataService();
        PollingMarketDataService mtGoxService = mtGoxExchange.getPollingMarketDataService();
        PollingMarketDataService bitcoinChartsService = bitcoinChartsExchange.getPollingMarketDataService();

        /*marketsServiceMap.put(Markets.BITCOINCHARTS, bitcoinChartsService);
        marketsServiceMap.put(Markets.BITCUREX, bitcurexService);
        marketsServiceMap.put(Markets.BITSTAMP, bitstampService);
        marketsServiceMap.put(Markets.BTCCHINA, btcchinaService);*/
        marketsServiceMap.put(Markets.BTCE, btceService);
        /*marketsServiceMap.put(Markets.CAMPBX, campBxService);*/
        //marketsServiceMap.put(Markets.CAVIRTEX, cavirtexService);
        /*marketsServiceMap.put(Markets.KRAKEN, krakenService);*/
        marketsServiceMap.put(Markets.MTGOX, mtGoxService);
    }


    @Override
    public double[] getBalance() {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends TickerShallowObject> T getLastPrice(Markets market, Currency cur) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends TickerShallowObject> T getPrice(Markets market, Currency currency) {
        // Get the latest ticker data showing BTC to USD
        Ticker ticker;
        TickerShallowObject result = null;
        try {
            ticker = marketsServiceMap.get(market).getTicker(Currencies.BTC, currency.name());
            result = new TickerFullLayoutObject(currency, ticker.getLast().getAmount().doubleValue(),
                    ticker.getTimestamp(), ticker.getAsk().getAmount().doubleValue(), -1d,
                    ticker.getBid().getAmount().doubleValue(), ticker.getHigh().getAmount().doubleValue(), -1d, -1d, -1d,
                    ticker.getLow().getAmount().doubleValue(), ticker.getVolume().doubleValue(), -1d);
        } catch (Throwable e) {
            System.out.println("error while invoking ticker service for:" + HistoryDownloader.getMarketIdentifierName(market, currency));
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return (T) (result != null ? result : new TickerShallowObject(currency, 0, null));
    }

    @Override
    public Set<Currency> getSupportedCurrencies(Markets market) {
        Set<Currency> result = new LinkedHashSet<>();
        List<CurrencyPair> supportedCurrencies = marketsServiceMap.get(market).getExchangeSymbols();
        for (CurrencyPair pair : supportedCurrencies) {
            try {
                Currency currency = Currency.valueOf(pair.counterCurrency);
                if (currency != Currency.BTC) {
                    result.add(currency);
                }
            } catch (Throwable e) {
                System.out.println("Cannot convert unknown currency: " + pair.toString());
            }
        }

        return result;
    }

    @Override
    public String getLag(Markets market) {
        Exchange exchange = marketsExchangeMap.get(market);

        if (exchange != null
                && exchange instanceof MtGoxExchange) {
            //currently only MtGox supports retrieving trading lag
            MtGoxV2 mtGoxV2 = RestProxyFactory.createProxy(MtGoxV2.class, exchange.getExchangeSpecification().getPlainTextUri());
            MtGoxLagWrapper mtGoxLagWrapper;
            try {
                mtGoxLagWrapper = mtGoxV2.getLag();
                return mtGoxLagWrapper.getResult();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        return "";
    }

    @Override
    public List<TradesFullLayoutObject> getTrades(Markets market, Currency currency, long previousTimestamp) {
        System.out.println("Getting all the trades since:" + previousTimestamp + " for market:" + HistoryDownloader.getMarketIdentifierName(market, currency));
        List<TradesFullLayoutObject> result = new ArrayList<>();
        try {
            //the SINCE parameter from the API can be sent here
            Trades trades =  marketsServiceMap.get(market).getTrades(Currencies.BTC, currency.name(), Long.valueOf(previousTimestamp + "000"));   //convert the timestamp to microsecond
            for (Trade trade : trades.getTrades()) {
                //(long tradeId, Date dateDate, double tradePrice, double tradeAmount, Currency currency, Currency tradeItem, TradeType type)
                TradesFullLayoutObject newTrade = new TradesFullLayoutObject(trade.getId(), trade.getTimestamp(),
                        trade.getPrice().getAmount().doubleValue(), trade.getTradableAmount().doubleValue(),
                        Currency.valueOf(trade.getTradableIdentifier()),
                       Currency.valueOf(trade.getTransactionCurrency()), TradeType.valueOf(trade.getType().name()));
                result.add(newTrade);
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("Got " + result.size() + " trades for market:" + HistoryDownloader.getMarketIdentifierName(market, currency));
        return result;
    }

    @Override
    public String withdrawBTC(double amount, String dest_address) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String sellBTC(double amount) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String buyBTC(double amount) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
