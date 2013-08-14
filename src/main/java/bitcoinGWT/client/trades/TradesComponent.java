package bitcoinGWT.client.trades;

import bitcoinGWT.client.BitcoinGWTServiceAsync;
import bitcoinGWT.shared.model.Constants;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import bitcoinGWT.shared.model.example.PostProperties;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfoBean;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.grid.*;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/9/13
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesComponent extends ContentPanel {
    private BitcoinGWTServiceAsync mainService;
    private FramedPanel root;
    private PagingLoader<PagingLoadConfig, PagingLoadResult<TradesFullLayoutObject>> gridLoader;
    private boolean initialLoad = true;
    private LiveGridView<TradesFullLayoutObject> liveGridView;

    private int lastLoadedTradesListSize = 0;

    public TradesComponent(BitcoinGWTServiceAsync mainService) {
        this.mainService = mainService;
        initComponent();
        initTradesTimer();
    }

    private void initComponent() {
        setExpanded(true);
        setCollapsible(true);
        setAnimCollapse(true);
        setTitleCollapse(true);

        RpcProxy<PagingLoadConfig, PagingLoadResult<TradesFullLayoutObject>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<TradesFullLayoutObject>>() {
            @Override
            public void load(final PagingLoadConfig loadConfig, final AsyncCallback<PagingLoadResult<TradesFullLayoutObject>> callback) {
                //autosort by price, in case there is no other sort criteria
                if (loadConfig.getSortInfo().size() == 0) {
                    List<SortInfoBean> sortInfos = new ArrayList<SortInfoBean>();
                    sortInfos.add(new SortInfoBean("price", SortDir.DESC));
                    loadConfig.setSortInfo(sortInfos);
                }
                mainService.getTradesForGrid(Currency.EUR, loadConfig, initialLoad, callback);

                if (initialLoad) {
                    initialLoad = false;
                }
                System.out.println(new Date() + " finished running get trades task");
            }
        };

        PostProperties props = GWT.create(PostProperties.class);

        ListStore<TradesFullLayoutObject> store = new ListStore<TradesFullLayoutObject>(new ModelKeyProvider<TradesFullLayoutObject>() {
            @Override
            public String getKey(TradesFullLayoutObject item) {
                return "" + item.getTradeId();
            }
        });

        gridLoader = new PagingLoader<PagingLoadConfig, PagingLoadResult<TradesFullLayoutObject>>(
                proxy);
        gridLoader.setRemoteSort(true);

        ColumnConfig<TradesFullLayoutObject, Currency> currencyColumn = new ColumnConfig<TradesFullLayoutObject, Currency>(props.currency(), 80, "Currency");
        ColumnConfig<TradesFullLayoutObject, Currency> tradeItemColumn = new ColumnConfig<TradesFullLayoutObject, Currency>(props.tradeItem(), 80, "Trade item");
        ColumnConfig<TradesFullLayoutObject, TradesFullLayoutObject.TradeType> typeColumn = new ColumnConfig<TradesFullLayoutObject, TradesFullLayoutObject.TradeType>(props.type(), 80, "Type");
        ColumnConfig<TradesFullLayoutObject, Double> priceColumn = new ColumnConfig<TradesFullLayoutObject, Double>(props.price(), 150, "Price");
        ColumnConfig<TradesFullLayoutObject, Double> amountColumn = new ColumnConfig<TradesFullLayoutObject, Double>(props.amount(), 150, "Amount");
        ColumnConfig<TradesFullLayoutObject, Date> dateColumn = new ColumnConfig<TradesFullLayoutObject, Date>(props.date(), 250, "Date");
        dateColumn.setCell(new DateCell(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT)));

        List<ColumnConfig<TradesFullLayoutObject, ?>> l = new ArrayList<ColumnConfig<TradesFullLayoutObject, ?>>();
        l.add(currencyColumn);
        l.add(tradeItemColumn);
        l.add(typeColumn);
        l.add(amountColumn);
        l.add(priceColumn);
        l.add(dateColumn);

        liveGridView = new LiveGridView<TradesFullLayoutObject>();
        //set the grid to hold 200 items in the cache
        liveGridView.setCacheSize(200);
        //liveGridView.setCacheSize(30);
        liveGridView.setForceFit(true);

        ColumnModel<TradesFullLayoutObject> cm = new ColumnModel<TradesFullLayoutObject>(l);

        Grid<TradesFullLayoutObject> view = new Grid<TradesFullLayoutObject>(store, cm);

        view.setLoadMask(true);
        view.setLoader(gridLoader);

        view.setView(liveGridView);

        root = new FramedPanel();
        root.setCollapsible(true);
        root.setHeadingText("Live Grid Example");
        //root.setPixelSize(600, 390);
        new Resizable(root);

        VerticalLayoutContainer con = new VerticalLayoutContainer();
        con.setBorders(true);
        con.add(view, new VerticalLayoutContainer.VerticalLayoutData(1, 1));

        ToolBar toolBar = new ToolBar();
        toolBar.add(new LiveToolItem(view));
        toolBar.addStyleName(ThemeStyles.getStyle().borderTop());
        toolBar.getElement().getStyle().setProperty("borderBottom", "none");

        con.add(toolBar, new VerticalLayoutContainer.VerticalLayoutData(1, 25));

        root.setWidget(con);

        //setWidget(root);

        setHeadingText("Trades");
    }

    @Override
    public Widget asWidget() {
        return root;
    }

    private void initTradesTimer() {
        Timer timer = new Timer() {

            //@Override
            public void run() {
                //if first time, load the initial grid size
                System.out.println(new Date() + " run get trades task");
                //check to see if there are any additional trades to load on the client
                mainService.getLastLoadedTradesSizeFromServer(Currency.EUR, new AsyncCallback<Integer>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        loadGrid(lastLoadedTradesListSize);
                    }

                    @Override
                    public void onSuccess(Integer loadedTradesListSize) {
                        //in case getting the trades size was successful
                        loadGrid(loadedTradesListSize);
                        //initTradesTimer();
                    }
                });

            }
        };

        timer.scheduleRepeating(Constants.TRADES_INTERVAL);
    }

    private void loadGrid(int loadedTradesListSize) {
        //load the trades from the server only if there are any differences. Or in case it's the initial loading
        if (initialLoad) {
            gridLoader.load(0, liveGridView.getCacheSize());
        } else if ((lastLoadedTradesListSize != loadedTradesListSize)) {//in case the server loaded trades size is different from the UI one, load the last trades from the server
            lastLoadedTradesListSize = loadedTradesListSize;
            gridLoader.load();
        }
    }
}
