package bitcoinGWT.client;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/10/13
 * Time: 11:59 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradeType;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import bitcoinGWT.shared.model.example.PostProperties;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sencha.gxt.core.client.resources.ThemeStyles;
import com.sencha.gxt.data.client.loader.RpcProxy;
import com.sencha.gxt.data.shared.ListStore;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoader;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.Resizable;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer.VerticalLayoutData;
import com.sencha.gxt.widget.core.client.grid.*;
import com.sencha.gxt.widget.core.client.toolbar.ToolBar;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LiveGridExample implements IsWidget, EntryPoint {

    private FramedPanel root;

    @Override
    public Widget asWidget() {
        if (root == null) {
            final ExampleServiceAsync service = GWT.create(ExampleService.class);

            RpcProxy<PagingLoadConfig, PagingLoadResult<TradesFullLayoutObject>> proxy = new RpcProxy<PagingLoadConfig, PagingLoadResult<TradesFullLayoutObject>>() {
                @Override
                public void load(PagingLoadConfig loadConfig, AsyncCallback<PagingLoadResult<TradesFullLayoutObject>> callback) {
                    service.getPosts(loadConfig, true, callback);
                }
            };

            PostProperties props = GWT.create(PostProperties.class);

            ListStore<TradesFullLayoutObject> store = new ListStore<TradesFullLayoutObject>(new ModelKeyProvider<TradesFullLayoutObject>() {
                @Override
                public String getKey(TradesFullLayoutObject item) {
                    return "" + item.getTradeId();
                }
            });

            final PagingLoader<PagingLoadConfig, PagingLoadResult<TradesFullLayoutObject>> gridLoader = new PagingLoader<PagingLoadConfig, PagingLoadResult<TradesFullLayoutObject>>(
                    proxy);
            gridLoader.setRemoteSort(true);

            ColumnConfig<TradesFullLayoutObject, Currency> currencyColumn = new ColumnConfig<TradesFullLayoutObject, Currency>(props.currency(), 150, "Currency");
            ColumnConfig<TradesFullLayoutObject, Currency> tradeItemColumn = new ColumnConfig<TradesFullLayoutObject, Currency>(props.tradeItem(), 150, "Trade item");
            ColumnConfig<TradesFullLayoutObject, TradeType> typeColumn = new ColumnConfig<TradesFullLayoutObject, TradeType>(props.type(), 150, "Type");
            ColumnConfig<TradesFullLayoutObject, Double> priceColumn = new ColumnConfig<TradesFullLayoutObject, Double>(props.price(), 150, "Price");
            ColumnConfig<TradesFullLayoutObject, Double> amountColumn = new ColumnConfig<TradesFullLayoutObject, Double>(props.amount(), 150, "Amount");
            ColumnConfig<TradesFullLayoutObject, Date> dateColumn = new ColumnConfig<TradesFullLayoutObject, Date>(props.date(), 150, "Date");
            dateColumn.setCell(new DateCell(DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_FULL)));

            List<ColumnConfig<TradesFullLayoutObject, ?>> l = new ArrayList<ColumnConfig<TradesFullLayoutObject, ?>>();
            l.add(currencyColumn);
            l.add(tradeItemColumn);
            l.add(typeColumn);
            l.add(amountColumn);
            l.add(priceColumn);
            l.add(dateColumn);

            final LiveGridView<TradesFullLayoutObject> liveGridView = new LiveGridView<TradesFullLayoutObject>();
            //set the grid to hold 200 items in the cache
            //liveGridView.setCacheSize(200);
            liveGridView.setCacheSize(30);
            liveGridView.setForceFit(true);

            ColumnModel<TradesFullLayoutObject> cm = new ColumnModel<TradesFullLayoutObject>(l);

            Grid<TradesFullLayoutObject> view = new Grid<TradesFullLayoutObject>(store, cm) {
                @Override
                protected void onAfterFirstAttach() {
                    super.onAfterFirstAttach();
                    Scheduler.get().scheduleDeferred(new ScheduledCommand() {
                        @Override
                        public void execute() {
                            gridLoader.load(0, liveGridView.getCacheSize());
                        }
                    });
                }
            };

            view.setLoadMask(true);
            view.setLoader(gridLoader);

            view.setView(liveGridView);

            root = new FramedPanel();
            root.setCollapsible(true);
            root.setHeadingText("Live Grid Example");
            root.setPixelSize(600, 390);
            new Resizable(root);

            VerticalLayoutContainer con = new VerticalLayoutContainer();
            con.setBorders(true);
            con.add(view, new VerticalLayoutData(1, 1));

            ToolBar toolBar = new ToolBar();
            toolBar.add(new LiveToolItem(view));
            //toolBar.addStyleName(ThemeStyles.getStyle().borderTop());
            toolBar.getElement().getStyle().setProperty("borderBottom", "none");

            con.add(toolBar, new VerticalLayoutData(1, 25));

            root.setWidget(con);
        }

        return root;
    }

    @Override
    public void onModuleLoad() {
        RootPanel.get().add(this);
    }
}
