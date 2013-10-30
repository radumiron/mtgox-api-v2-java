package bitcoinGWT.client.candlestick_chart;

import java.util.Date;

import bitcoinGWT.shared.model.Constants;
import com.google.gwt.core.client.JsDate;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.googlecode.gwt.charts.client.*;
import com.googlecode.gwt.charts.client.controls.Dashboard;
import com.googlecode.gwt.charts.client.controls.filter.ChartRangeFilter;
import com.googlecode.gwt.charts.client.controls.filter.ChartRangeFilterOptions;
import com.googlecode.gwt.charts.client.controls.filter.ChartRangeFilterState;
import com.googlecode.gwt.charts.client.controls.filter.ChartRangeFilterStateRange;
import com.googlecode.gwt.charts.client.controls.filter.ChartRangeFilterUi;
import com.googlecode.gwt.charts.client.corechart.CandlestickChartOptions;
import com.googlecode.gwt.charts.client.options.ChartArea;
import com.googlecode.gwt.charts.client.options.Legend;
import com.googlecode.gwt.charts.client.options.LegendPosition;

public class ChartRangeFilterExample extends DockLayoutPanel {
	private Dashboard dashboard;
	private ChartWrapper<CandlestickChartOptions> candlestickChart;
	private ChartRangeFilter numberRangeFilter;
    private DataTable data;

    public ChartRangeFilterExample() {
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
		chartRangeFilterUi.setMinRangeSize(2 * 24 * 60 * 60 * 1000); // 2 days in milliseconds

		chartRangeFilterOptions.setUi(chartRangeFilterUi);

		ChartRangeFilterStateRange stateRange = ChartRangeFilterStateRange.create();
		stateRange.setStart(new Date((long) JsDate.create(2012, 2, 9).getTime()));
		stateRange.setEnd(new Date((long) JsDate.create(2012, 3, 20).getTime()));
		ChartRangeFilterState controlState = ChartRangeFilterState.create();
		controlState.setRange(stateRange);
		numberRangeFilter.setState(controlState);
		numberRangeFilter.setOptions(chartRangeFilterOptions);

		// Set chart options
		CandlestickChartOptions lineChartOptions = CandlestickChartOptions.create();
		lineChartOptions.setLegend(Legend.create(LegendPosition.NONE));
		lineChartOptions.setChartArea(chartArea);
		candlestickChart.setOptions(lineChartOptions);

		// Generate random data
        data = DataTable.create();
        data.addColumn(ColumnType.DATETIME);
        data.addColumn(ColumnType.NUMBER);
        data.addColumn(ColumnType.NUMBER);
        data.addColumn(ColumnType.NUMBER);
        data.addColumn(ColumnType.NUMBER);

        data.addRows(1);
        //data.setValue(row_id, low, open, close, high)
        data.setValue(0, 0, new Date((long) JsDate.create(2012, 2, 9).getTime()));
        data.setValue(0, 1, 20);
        data.setValue(0, 2, 28);
        data.setValue(0, 3, 38);
        data.setValue(0, 4, 45);

		// Draw the chart
		dashboard.bind(numberRangeFilter, candlestickChart);
		dashboard.draw(data);
	}

    private void startTimer() {
        Timer timer = new Timer() {

            //@Override
            public void run() {
                refreshChart();
            }
        };
        //timer.schedule(Constants.INITIAL_UI_TRADES_DELAY);
        timer.scheduleRepeating(Constants.CANDLESTICK_CHART_TRADES_RETRIEVAL_INTERVAL);
    }

    public void refreshChart(){
        int originalRows = data.getNumberOfRows();
        data.addRows(1);

        double open, close = 300;
        double low, high;
        double change = (Math.sin(originalRows / 2.5 + Math.PI) + Math.sin(originalRows / 3) - Math.cos(originalRows * 0.7)) * 150;
        change = change >= 0 ? change + 10 : change - 10;
        open = close;
        close = Math.max(50, open + change);
        low = Math.min(open, close) - (Math.cos(originalRows * 1.7) + 1) * 15;
        low = Math.max(0, low);
        high = Math.max(open, close) + (Math.cos(originalRows * 1.3) + 1) * 15;
        Date date = new Date((long) JsDate.create(2012, 1, originalRows).getTime());
        data.setValue(originalRows, 0, date);
        data.setValue(originalRows, 1, low);
        data.setValue(originalRows, 2, open);
        data.setValue(originalRows, 3, close);
        data.setValue(originalRows, 4, high);

        dashboard.draw(data);
    }
}