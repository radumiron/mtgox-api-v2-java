package bitcoinGWT.client.ticker;

import bitcoinGWT.client.BitcoinGWTServiceAsync;
import bitcoinGWT.client.CustomAsyncCallback;
import bitcoinGWT.shared.model.Constants;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TickerFullLayoutObject;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Label;
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

    private Label tickerLabel;
    private BitcoinGWTServiceAsync mainService;

    public TickerComponent(BitcoinGWTServiceAsync mainService) {
        this.mainService = mainService;
        initComponents();
        initTickerTimer();
    }

    private void initComponents() {
        setExpanded(true);
        setCollapsible(true);
        setAnimCollapse(true);
        setTitleCollapse(true);

        //setHeaderVisible(false);
        setHeadingText("Ticker");

        tickerLabel = new Label("");
        tickerLabel.addStyleName("ticker-label");

        HorizontalLayoutContainer northContainer = new HorizontalLayoutContainer();
        northContainer.add(tickerLabel, new HorizontalLayoutContainer.HorizontalLayoutData());
        northContainer.add(new SimpleContainer(), new HorizontalLayoutContainer.HorizontalLayoutData(1, 1));

        add(northContainer);
    }

    private void initTickerTimer() {
        Timer timer = new Timer() {

            @Override
            public void run() {
                mainService.getPrice(Currency.EUR, new CustomAsyncCallback<TickerFullLayoutObject>() {

                    @Override
                    public void onSuccess(TickerFullLayoutObject result) {
                        tickerLabel.setText(String.valueOf(result.getPrice()));
                    }
                });
            }
        };

        timer.scheduleRepeating(Constants.TICKER_INTERVAL);
    }
}
