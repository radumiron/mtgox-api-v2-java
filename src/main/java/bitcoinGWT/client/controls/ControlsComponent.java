package bitcoinGWT.client.controls;

import bitcoinGWT.client.CustomAsyncCallback;
import bitcoinGWT.client.events.CurrencyChangeEvent;
import bitcoinGWT.client.util.UiUtils;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.IndexedPanel;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.core.client.util.ToggleGroup;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.button.ToggleButton;
import com.sencha.gxt.widget.core.client.container.AccordionLayoutContainer;
import com.sencha.gxt.widget.core.client.container.SimpleContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/9/13
 * Time: 11:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlsComponent extends ContentPanel {

    private ContentPanel currencies;
    private Markets selectedMarket;
    private Currency selectedCurrency;

    public ControlsComponent() {
        initComponents();
    }

    private void initComponents() {
        setExpanded(true);
        setCollapsible(true);
        setAnimCollapse(true);
        setTitleCollapse(true);

        //setHeadingText("Controls");
        //setTitle("Controls");

        AccordionLayoutContainer con = new AccordionLayoutContainer();
        con.setExpandMode(AccordionLayoutContainer.ExpandMode.MULTI);
        add(con);

        AccordionLayoutContainer.AccordionLayoutAppearance appearance = GWT.<AccordionLayoutContainer.AccordionLayoutAppearance> create(AccordionLayoutContainer.AccordionLayoutAppearance.class);

        ContentPanel markets = new ContentPanel(appearance);
        markets.setAnimCollapse(false);
        markets.setHeadingText("Markets");
        con.add(markets);
        con.setActiveWidget(markets);

        currencies = new ContentPanel(appearance);
        currencies.setAnimCollapse(false);
        currencies.setHeadingText("Currencies");
        con.add(currencies);

        ContentPanel intervals = new ContentPanel(appearance);
        intervals.setAnimCollapse(false);
        intervals.setHeadingText("Intervals");
        con.add(intervals);

        createMarketsComponent(markets);
    }

    private void createMarketsComponent(ContentPanel marketsContentPanel) {
        VerticalLayoutContainer container = new VerticalLayoutContainer();
        //add one button for each Markets enum entry
        ToggleGroup group = new ToggleGroup();
        Markets[] markets = Markets.values();
        for (final Markets market : markets) {
            ToggleButton marketButton = new ToggleButton(/*market.name()*/);
            marketButton.setIcon(UiUtils.getImageForMarket(market));
            marketButton.addSelectHandler(new SelectEvent.SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    //set the current selected market
                    selectedMarket = market;
                    UiUtils.getAsyncService().getSupportedCurrencies(market, new CustomAsyncCallback<List<Currency>>() {
                        @Override
                        public void onSuccess(List<Currency> result) {
                            createCurrenciesComponent(currencies, result);
                        }
                    });
                }
            });
            container.add(marketButton, new VerticalLayoutContainer.VerticalLayoutData(1, 32, new Margins(1)));
            group.add(marketButton);

            //set default selected button
            if (market == Markets.MTGOX) {
                marketButton.setValue(true);
                marketButton.fireEvent(new SelectEvent());
            }
        }
        //add a spacing at the end
        container.add(new SimpleContainer(), new VerticalLayoutContainer.VerticalLayoutData(1, 2));


        marketsContentPanel.setWidget(container);
    }

    public void createCurrenciesComponent(ContentPanel currenciesPanel, List<Currency> currenciesList) {
        VerticalLayoutContainer container = new VerticalLayoutContainer();

        removeAllComponents(currenciesPanel);
        ToggleGroup group = new ToggleGroup();

        Map<Currency, ToggleButton> previousSelectedCurrencyToButton = new HashMap<Currency, ToggleButton>();
        for (final Currency currency : currenciesList) {
            ToggleButton currencyButton = new ToggleButton(currency.name());
            currencyButton.addSelectHandler(new SelectEvent.SelectHandler() {
                @Override
                public void onSelect(SelectEvent event) {
                    selectedCurrency = currency;
                    //send event on EventBus to the chart & trades components
                    UiUtils.EVENT_BUS.fireEvent(new CurrencyChangeEvent(selectedMarket, selectedCurrency));
                }
            });
            previousSelectedCurrencyToButton.put(currency, currencyButton);

            container.add(currencyButton, new VerticalLayoutContainer.VerticalLayoutData(32, 32, new Margins(2)));
            group.add(currencyButton);
        }

        //add a spacing at the end
        container.add(new SimpleContainer(), new VerticalLayoutContainer.VerticalLayoutData(1, 2));

        //this means we already have a currency selected, which is also valid for the current selected currency
        if (selectedCurrency != null) {
            ToggleButton button = previousSelectedCurrencyToButton.get(selectedCurrency);
            //if the selected currency is valid for the current market
            if (button != null) {
                toggleButton(button);
            } else { //if we don't have any previously selected currency, select the first one
                toggleButton(previousSelectedCurrencyToButton.get(currenciesList.get(0)));
            }
        } else {
            toggleButton(previousSelectedCurrencyToButton.get(currenciesList.get(0)));
        }

        currenciesPanel.setWidget(container);
    }

    private void toggleButton(ToggleButton toggleButton) {
        toggleButton.setValue(true);
        toggleButton.fireEvent(new SelectEvent());
    }

    private void removeAllComponents(IndexedPanel panel) {
        for (int i = 0; i < panel.getWidgetCount(); i++) {
            panel.remove(i);
        }
    }
}
