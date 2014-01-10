package trading.bitcoinXChange;

import bitcoinGWT.shared.model.TickerFullLayoutObject;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.dto.marketdata.Trades;
import trading.api_interfaces.TradeInterface;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TickerShallowObject;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.xeiam.xchange.Exchange;
import com.xeiam.xchange.ExchangeFactory;
import com.xeiam.xchange.currency.Currencies;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.service.polling.PollingMarketDataService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import trading.mtgox_api.com.mtgox.api.constants.TradeParams;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @PostConstruct
    private void initTradingObject() {
        // Use the factory to get the version 2 MtGox exchange API using default settings
        Exchange mtGox = ExchangeFactory.INSTANCE.createExchange("com.xeiam.xchange.mtgox.v2.MtGoxExchange");

        // Interested in the public polling market data feed (no authentication)
        mtGoxService = mtGox.getPollingMarketDataService();
    }


    @Override
    public double[] getBalance() {
        return new double[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends TickerShallowObject> T getLastPrice(Currency cur) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public <T extends TickerShallowObject> T getPrice(Currency currency) {
        // Get the latest ticker data showing BTC to USD
        Ticker ticker = null;
        TickerShallowObject result = null;
        try {
            ticker = mtGoxService.getTicker(Currencies.BTC, Currencies.EUR);
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
    public String getLag() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<TradesFullLayoutObject> getTrades(Currency currency, long previousTimestamp) {
        List<TradesFullLayoutObject> result = new ArrayList<>();
        try {
            //the SINCE parameter from the API can be sent here
            Trades trades = mtGoxService.getTrades(Currencies.BTC, Currencies.EUR, Long.valueOf(Long.toString(previousTimestamp) + "000"));   //convert the timestamp to microsecond
            for (Trade trade : trades.getTrades()) {
                //(long tradeId, Date dateDate, double tradePrice, double tradeAmount, Currency currency, Currency tradeItem, TradeType type)
                TradesFullLayoutObject newTrade = new TradesFullLayoutObject(trade.getId(), trade.getTimestamp(),
                        trade.getPrice().getAmount().doubleValue(), trade.getTradableAmount().doubleValue(),
                        Currency.valueOf(trade.getTradableIdentifier()),
                       Currency.valueOf(trade.getTransactionCurrency()), TradesFullLayoutObject.TradeType.valueOf(trade.getType().name()));
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
