package bitcoinGWT.client.chart2;

import bitcoinGWT.client.util.UiUtils;
import bitcoinGWT.shared.model.ChartElement;
import bitcoinGWT.shared.model.Constants;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TimeInterval;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.googlecode.gwt.charts.client.*;
import com.googlecode.gwt.charts.client.controls.Dashboard;
import com.googlecode.gwt.charts.client.controls.filter.*;
import com.googlecode.gwt.charts.client.corechart.CandlestickChartOptions;
import com.googlecode.gwt.charts.client.options.ChartArea;
import com.googlecode.gwt.charts.client.options.Legend;
import com.googlecode.gwt.charts.client.options.LegendPosition;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class ChartComponent2 extends DockLayoutPanel {
    private Dashboard dashboard;
    private ChartWrapper<CandlestickChartOptions> candlestickChart;
    private ChartRangeFilter numberRangeFilter;
    private DataTable data;
    private ChartRangeFilterStateRange stateRange;
    private Timer timer;
    private Long timeOfLastTrade = null;

    private boolean initialLoad = true;

    public ChartComponent2() {
        super(Unit.PX);
        initialize();
        startTimer();
    }

    private void initialize() {
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CONTROLS);
        chartLoader.loadApi(new Runnable() {

            @Override
            public void run() {
                addNorth(getDashboardWidget(), 0);
                addSouth(getNumberRangeFilter(), 100);
                add(getCandlestickChart());
                draw();
            }
        });
    }

    private Dashboard getDashboardWidget() {
        if (dashboard == null) {
            dashboard = new Dashboard();
        }
        return dashboard;
    }

    private ChartWrapper<CandlestickChartOptions> getCandlestickChart() {
        if (candlestickChart == null) {
            candlestickChart = new ChartWrapper<CandlestickChartOptions>();
            candlestickChart.setChartType(ChartType.CANDLESTICK);
        }
        return candlestickChart;
    }

    private ChartRangeFilter getNumberRangeFilter() {
        if (numberRangeFilter == null) {
            numberRangeFilter = new ChartRangeFilter();
        }
        return numberRangeFilter;
    }

    private void draw() {
        // Set control options
        ChartRangeFilterOptions chartRangeFilterOptions = ChartRangeFilterOptions.create();
        chartRangeFilterOptions.setFilterColumnIndex(0); // Filter by the date axis
        CandlestickChartOptions controlChartOptions = CandlestickChartOptions.create();
        controlChartOptions.setHeight(100);
        controlChartOptions.setEnableInteractivity(true);
        ChartArea chartArea = ChartArea.create();
        chartArea.setWidth("90%");
        chartArea.setHeight("90%");
        controlChartOptions.setChartArea(chartArea);

        ChartRangeFilterUi chartRangeFilterUi = ChartRangeFilterUi.create();
        chartRangeFilterUi.setChartType(ChartType.CANDLESTICK);
        chartRangeFilterUi.setChartOptions(controlChartOptions);
        //chartRangeFilterUi.setMinRangeSize(2 * 24 * 60 * 60 * 1000); // 2 days in milliseconds

        chartRangeFilterOptions.setUi(chartRangeFilterUi);

        stateRange = ChartRangeFilterStateRange.create();
        //stateRange.setStart(new Date((long) JsDate.create(2012, 2, 9).getTime()));
        //stateRange.setEnd(new Date((long) JsDate.create(2012, 3, 20).getTime()));
        ChartRangeFilterState controlState = ChartRangeFilterState.create();
        controlState.setRange(stateRange);
        numberRangeFilter.setState(controlState);
        numberRangeFilter.setOptions(chartRangeFilterOptions);

        // Set chart options
        CandlestickChartOptions lineChartOptions = CandlestickChartOptions.create();
        lineChartOptions.setLegend(Legend.create(LegendPosition.NONE));
        lineChartOptions.setChartArea(chartArea);
        candlestickChart.setOptions(lineChartOptions);

        //create the data table
        data = DataTable.create();
        data.addColumn(ColumnType.DATETIME);
        data.addColumn(ColumnType.NUMBER);
        data.addColumn(ColumnType.NUMBER);
        data.addColumn(ColumnType.NUMBER);
        data.addColumn(ColumnType.NUMBER);

        // Draw the chart
        dashboard.bind(numberRangeFilter, candlestickChart);
    }

    private void setServerData() {
        System.out.println(new Date() + ": get chart elements");

        //the first time this happens, initialLoad = true, timeOfLastTrade = null.
        UiUtils.getAsyncService().getTradesForChart(Currency.EUR, timeOfLastTrade, initialLoad, TimeInterval.TEN_MINUTES, new AsyncCallback<Set<ChartElement>>() {
            @Override
            public void onFailure(Throwable caught) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess(Set<ChartElement> result) {
                System.out.println(new Date() + ": received " + result.size() + " chart elements");
                if (initialLoad) {  //set the initial load to false, we just want diffs right now.
                    initialLoad = false;
                }

                //data.setValue(row_id, low, open, close, high)
                Iterator<ChartElement> it = result.iterator();
                while (it.hasNext()) {
                    ChartElement trade = it.next();
                    int currentRow = data.getNumberOfRows();
                    data.addRow();
                    data.setValue(currentRow, 0, trade.getElementDate().getEnd());
                    data.setValue(currentRow, 1, trade.getLow());
                    data.setValue(currentRow, 2, trade.getOpen());
                    data.setValue(currentRow, 3, trade.getClose());
                    data.setValue(currentRow, 4, trade.getHigh());

                    //check if the current chart element is the last
                    if (!it.hasNext()) {
                        //save the time of the last trade item
                        timeOfLastTrade = trade.getTimeOfLastTrade().getTime();
                    }
                }

                //in case there were no trades, set the timeOfLastTrade to current time
                /*if (result.isEmpty()) {
                    timeOfLastTrade = new Date().getTime();
                }*/

                dashboard.draw(data);

                //call the server again, after an interval.
                startTimer();
                System.out.println();
            }
        });
    }

    private void startTimer() {
        timer = new Timer() {

            //@Override
            public void run() {
                setServerData();
            }
        };
        timer.schedule(Constants.TRADES_RETRIEVAL_INTERVAL);
        //timer.scheduleRepeating(Constants.TRADES_RETRIEVAL_INTERVAL);
    }
}