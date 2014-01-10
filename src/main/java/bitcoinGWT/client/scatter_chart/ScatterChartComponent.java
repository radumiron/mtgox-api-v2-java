package bitcoinGWT.client.scatter_chart;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 11/7/13
 * Time: 12:13 AM
 * To change this template use File | Settings | File Templates.
 */

import bitcoinGWT.client.candlestick_chart.CustomDataTable;
import bitcoinGWT.client.candlestick_chart.CustomTooltip;
import bitcoinGWT.client.util.UiUtils;
import bitcoinGWT.shared.model.ChartElement;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TimeInterval;
import bitcoinGWT.shared.model.TimeWindow;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.googlecode.gwt.charts.client.*;
import com.googlecode.gwt.charts.client.controls.Dashboard;
import com.googlecode.gwt.charts.client.controls.filter.*;
import com.googlecode.gwt.charts.client.corechart.AreaChartOptions;
import com.googlecode.gwt.charts.client.corechart.ScatterChartOptions;
import com.googlecode.gwt.charts.client.options.*;
import com.sencha.gxt.widget.core.client.container.BorderLayoutContainer;

import java.util.Date;
import java.util.Iterator;
import java.util.Set;

public class ScatterChartComponent extends BorderLayoutContainer {
    private Dashboard dashboard;
    private ChartWrapper<AreaChartOptions> candlestickChart;
    private ChartRangeFilter numberRangeFilter;
    private CustomDataTable data;
    private ChartRangeFilterStateRange stateRange;
    private Timer timer;
    private Long timeOfLastTrade = null;

    private boolean initialLoad = true;

    public ScatterChartComponent() {
        //super(Unit.PX);
        initialize();

        //trigger the chat init before the timer
        setServerData();

        //start the timer
        //startTimer();
    }

    private void initialize() {
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CONTROLS);
        chartLoader.loadApi(new Runnable() {

            @Override
            public void run() {
                setNorthWidget(getDashboardWidget(), new BorderLayoutData(0));
                setCenterWidget(getCandlestickChart());
                setSouthWidget(getNumberRangeFilter(), new BorderLayoutData(100));
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

    private ChartWrapper<AreaChartOptions> getCandlestickChart() {
        if (candlestickChart == null) {
            candlestickChart = new ChartWrapper<AreaChartOptions>();
            candlestickChart.setChartType(ChartType.AREA);
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

        AreaChartOptions controlChartOptions = AreaChartOptions.create();
        controlChartOptions.setHeight(100);
        /*BackgroundColor rising = BackgroundColor.create();
        rising.setFill("00CC00");
        rising.setStroke("00CC00");
        controlChartOptions.setRisingColor(rising);

        BackgroundColor falling = BackgroundColor.create();
        falling.setFill("D00000");
        falling.setStroke("D00000");
        controlChartOptions.setFallingColor(falling);
*/
        ChartArea chartArea = ChartArea.create();
        chartArea.setWidth("90%");
        chartArea.setHeight("90%");
        controlChartOptions.setChartArea(chartArea);

        ChartRangeFilterUi chartRangeFilterUi = ChartRangeFilterUi.create();
        chartRangeFilterUi.setChartType(ChartType.AREA);
        chartRangeFilterUi.setChartOptions(controlChartOptions);
        //chartRangeFilterUi.setMinRangeSize(TimeInterval.ONE_HOUR.getMinutes() * 60 * 1000); //one hour


        //set the lines in the chart control
        //0 - date axis
        //1,2,3,4 = low, open, close, high
        JsArrayString stringArray = JsonUtils.unsafeEval("{\n" +
                "           'columns': [0, 2, 3]\n" +
                "         }");
        chartRangeFilterUi.setChartView(stringArray);
        chartRangeFilterOptions.setUi(chartRangeFilterUi);

        stateRange = ChartRangeFilterStateRange.create();

        ChartRangeFilterState controlState = ChartRangeFilterState.create();
        controlState.setRange(stateRange);
        numberRangeFilter.setState(controlState);
        numberRangeFilter.setOptions(chartRangeFilterOptions);

        // Set chart options
        AreaChartOptions lineChartOptions = AreaChartOptions.create();
        lineChartOptions.setLegend(Legend.create(LegendPosition.NONE));
        lineChartOptions.setChartArea(chartArea);
        //setChartTooltip(lineChartOptions);
        //setAnimation(lineChartOptions);
        candlestickChart.setOptions(lineChartOptions);

        //create the data table
        data = CustomDataTable.create();
        data.addColumn(ColumnType.DATETIME);
        data.addColumn(ColumnType.NUMBER);
        data.addColumn(ColumnType.NUMBER);
        data.addColumn(ColumnType.NUMBER);
        data.addColumn(ColumnType.NUMBER);
        //data.addTooltipColumn(data, true);

        // Draw the chart
        dashboard.bind(numberRangeFilter, candlestickChart);
    }

    private void setAnimation(ScatterChartOptions lineChartOptions) {
        Animation animation = Animation.create();
        animation.setDuration(100);
        animation.setEasing(AnimationEasing.OUT);

        lineChartOptions.setAnimation(animation);
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

                //hold a reference to the last trade
                ChartElement lastTrade = null;

                //data.setValue(row_id, low, open, close, high)
                Iterator<ChartElement> it = result.iterator();
                while (it.hasNext()) {
                    ChartElement trade = it.next();
                    System.out.println(trade);
                    int currentRow = data.getNumberOfRows();
                    data.addRows(1);
                    data.setValue(currentRow, 0, getChartElementEndDate(trade));
                    data.setValue(currentRow, 1, trade.getLow());
                    data.setValue(currentRow, 2, trade.getOpen());
                    data.setValue(currentRow, 3, trade.getClose());
                    data.setValue(currentRow, 4, trade.getHigh());
                    //data.setValue(currentRow, 5, getTooltipFromChartElement(trade));

                    //check if the current chart element is the last
                    if (!it.hasNext()) {
                        //save the time of the last trade item
                        lastTrade = trade;
                    }
                }

                if (lastTrade != null) {
                    timeOfLastTrade = lastTrade.getTimeOfLastTrade().getTime();
                    createChartRangeWindow(lastTrade, TimeInterval.THREE_HOURS);
                }

                dashboard.draw(data);

                //call the server again, after an interval.
                startTimer();
                System.out.println();
            }
        });
    }

    private void createChartRangeWindow(ChartElement lastTrade, TimeInterval interval) {
        //take the values of the current chart range
        Date currentStart = stateRange.getStartDate();
        Date currentEnd = stateRange.getEndDate();

        if (currentStart == null && currentEnd == null) {
            stateRange.setStart(new Date(getChartElementEndDate(lastTrade).getTime() - (interval.getMinutes() * 60 * 1000)));
            stateRange.setEnd(getChartElementEndDate(lastTrade));
        } else {
            /*//calculate the difference between new trade time and current time
            long timeDifference = getChartElementEndDate(lastTrade).getTime() - currentEnd.getTime();

            //we have to "shift" the new window to display the last trade

            //the new start is the old start + time Difference
            stateRange.setStart(new Date(currentStart.getTime() + timeDifference));
            //the new end is the time of the last trade*/
            stateRange.setEnd(getChartElementEndDate(lastTrade));
        }
    }

    private void setChartTooltip(ScatterChartOptions lineChartOptions) {
        CustomTooltip tooltip = CustomTooltip.create();
        tooltip.setHtml(true);
        lineChartOptions.setTooltip(tooltip);
    }

    private String getTooltipFromChartElement(ChartElement element) {
        StringBuilder sb = new StringBuilder();
        DateTimeFormat format = DateTimeFormat.getFormat(("HH:mm"));
        sb.append("<style type='text/css'>")
                .append("td {font-family:Arial; color:#282828; font-size:10pt;} ")
                .append(".date {font-family:Arial; color:#282828; font-size:10pt; font-weight:bold;} ")
                .append("</style>");

        sb.append("<div class=\"date\">").append("@").append(format.format(getChartElementEndDate(element))).append("\n").append("\n").append("</div>")
                .append("<table>")
                .append("<tr>")
                .append("<td>").append("Low:").append("</td>")
                .append("<td>").append(element.getLow()).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append("Open:").append("</td>")
                .append("<td>").append(element.getOpen()).append("</td>")
                .append("</tr>")
                .append("<tr>")
                .append("<td>").append("Close:").append("</td>")
                .append("<td>").append(element.getClose()).append("</td>")
                .append("<tr>")
                .append("<td>").append("High:").append("</td>")
                .append("<td>").append(element.getHigh()).append("</td>")
                .append("</tr>")
                .append("</table>");

        return sb.toString();
    }

    private Date getChartElementEndDate(ChartElement chartElement) {
        return chartElement.getElementDate().getStart();
    }

    private void startTimer() {
        timer = new Timer() {

            //@Override
            public void run() {
                //setServerData();
                refreshChart();
            }
        };
        //timer.schedule(Constants.CANDLESTICK_CHART_TRADES_RETRIEVAL_INTERVAL);

        timer.scheduleRepeating(5 * 1000);
    }

    public void refreshChart(){
        int originalRows = data.getNumberOfRows();
        data.addRows(1);

        double open, close = 190;
        double low, high;
        double change = (Math.sin(originalRows / 2.5 + Math.PI) + Math.sin(originalRows / 3) - Math.cos(originalRows * 0.7)) * 2;
        change = change >= 0 ? change + 5 : change - 5;
        open = close;
        close = Math.max(50, open + change);
        low = Math.min(open, close) - (Math.cos(originalRows * 1.7) + 1) * 2;
        low = Math.max(0, low);
        high = Math.max(open, close) + (Math.cos(originalRows * 1.3) + 1) * 2;

        Date last = new Date();
        ChartElement trade = new ChartElement(open, close, low, high, 100, new TimeWindow(new Date((new Date().getTime() - 5 * 1000)), last), last, TimeInterval.TEN_MINUTES);

        System.out.println(trade);

        data.setValue(originalRows, 0, getChartElementEndDate(trade));
        data.setValue(originalRows, 1, low);
        data.setValue(originalRows, 2, open);
        data.setValue(originalRows, 3, close);
        data.setValue(originalRows, 4, high);

        dashboard.draw(data);

        createChartRangeWindow(trade, TimeInterval.TEN_MINUTES);

        dashboard.redraw();

        //startTimer();
    }
}