package trading.bitcoinXChange;

import bitcoinGWT.shared.model.*;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.bitcoincharts.BitcoinChartsExchange;
import com.xeiam.xchange.bitcoincharts.service.polling.BitcoinChartsPollingMarketDataService;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import com.xeiam.xchange.mtgox.v2.MtGoxExchange;
import com.xeiam.xchange.mtgox.v2.MtGoxV2;
import com.xeiam.xchange.mtgox.v2.dto.trade.polling.MtGoxLagWrapper;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import si.mazi.rescu.RestProxyFactory;
import trading.api_interfaces.TradeInterface;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/9/14
 * Time: 9:37 PM
 * To change this template use File | Settings | File Templates.
 */
@Component
@Primary
public class XChangeTrading implements TradeInterface {

    private PollingMarketDataService mtGoxService;
    private BitcoinChartsPollingMarketDataService bitcoinChartsService;

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
        Exchange mtGoxExchange = ExchangeFactory.INSTANCE.createExchange(MtGoxExchange.class.getName());
        Exchange bitcoinChartsExchange = ExchangeFactory.INSTANCE.createExchange(BitcoinChartsExchange.class.getName());

        marketsExchangeMap.put(Markets.MTGOX, mtGoxExchange);
        //marketsExchangeMap.put(Markets.BITCOIN_CHARTS, bitcoinChartsExchange);

        // Interested in the public polling market data feed (no authentication)
        mtGoxService = mtGoxExchange.getPollingMarketDataService();

        bitcoinChartsService = (BitcoinChartsPollingMarketDataService) bitcoinChartsExchange.getPollingMarketDataService();

        marketsServiceMap.put(Markets.MTGOX, mtGoxService);
        //marketsServiceMap.put(Markets.BITCOIN_CHARTS, bitcoinChartsService);
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
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        return (T) (result != null ? result : new TickerShallowObject(currency, 0, null));
    }

    @Override
    public List<Currency> getSupportedCurrencies(Markets market) {
        List<Currency> result = new ArrayList<>();
        List<CurrencyPair> supportedCurrencies = marketsServiceMap.get(market).getExchangeSymbols();
        for (CurrencyPair pair : supportedCurrencies) {
            try {
                result.add(Currency.valueOf(pair.counterCurrency));
            } catch (Exception e) {
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
