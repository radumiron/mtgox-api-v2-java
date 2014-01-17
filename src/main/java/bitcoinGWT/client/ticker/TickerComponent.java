package bitcoinGWT.client.ticker;

import bitcoinGWT.client.BitcoinGWTServiceAsync;
import bitcoinGWT.client.CustomAsyncCallback;
import bitcoinGWT.client.events.CurrencyChangeEvent;
import bitcoinGWT.client.events.CurrencyChangeEventHandler;
import bitcoinGWT.client.util.UiUtils;
import bitcoinGWT.shared.model.Constants;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import bitcoinGWT.shared.model.TickerFullLayoutObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/2/13
 * Time: 10:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class TickerComponent extends ContentPanel {

    private static final String DEFAULT_TICKER_VALUE = "N/A";

    private Label tickerLabel;
    private Label tickerCurrency;
    private BitcoinGWTServiceAsync mainService;

    private Markets selectedMarket;
    private Currency selectedCurrency;
    private HorizontalLayoutContainer northContainer;

    public TickerComponent(BitcoinGWTServiceAsync mainService) {
        this.mainService = mainService;
        initComponents();
        initListeners();
        initTickerTimer();
    }

    private void initListeners() {
        UiUtils.EVENT_BUS.addHandler(CurrencyChangeEvent.TYPE, new CurrencyChangeEventHandler()     {
            @Override
            public void onCurrencyChanged(CurrencyChangeEvent currencyChangeEvent) {
                selectedMarket = currencyChangeEvent.getMarket();
                selectedCurrency = currencyChangeEvent.getCurrency();

                tickerCurrency.setText(selectedCurrency.name() + "/" + Currency.BTC.name());
                //hide the label until the ticker comes from the server
                tickerCurrency.setVisible(false);

                //reset the ticker label to the default one, until a new ticker value is loaded
                tickerLabel.setText(DEFAULT_TICKER_VALUE);
            }
        });
    }

    private void initComponents() {
        setExpanded(true);
        setCollapsible(true);
        setAnimCollapse(true);
        setTitleCollapse(true);

        //setHeaderVisible(false);
        setHeadingText("Ticker");

        tickerLabel = new Label(DEFAULT_TICKER_VALUE);
        tickerLabel.setStyleName("ticker-label");

        tickerCurrency = new Label("");

        northContainer = new HorizontalLayoutContainer();
        northContainer.add(tickerLabel, new HorizontalLayoutContainer.HorizontalLayoutData());
        northContainer.add(tickerCurrency, new HorizontalLayoutContainer.HorizontalLayoutData(-1, -1, new Margins(15, 0, 2, 5)));
        northContainer.add(new SimpleContainer(), new HorizontalLayoutContainer.HorizontalLayoutData(1, 1));

        add(northContainer);
    }

    private void initTickerTimer() {
        Timer timer = new Timer() {

            @Override
            public void run() {
                //while we don't have a market/currency selected, don't load anything
                if (selectedMarket == null && selectedCurrency == null) {
                    return;
                }
                mainService.getPrice(selectedMarket, selectedCurrency, new CustomAsyncCallback<TickerFullLayoutObject>() {
                    @Override
                    public void onSuccess(TickerFullLayoutObject result) {
                        tickerLabel.setText(String.valueOf(result.getPrice()));
                        tickerCurrency.setVisible(true);
                        northContainer.forceLayout();
                    }
                });
            }
        };

        timer.scheduleRepeating(Constants.TICKER_INTERVAL);
    }
}
