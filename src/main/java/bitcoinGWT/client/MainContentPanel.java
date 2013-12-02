package bitcoinGWT.client;

import bitcoinGWT.client.chart.CandleStickChart;
import bitcoinGWT.client.chart.ChartComponent;
import bitcoinGWT.client.candlestick_chart.ChartComponent2;
import bitcoinGWT.client.controls.ControlsComponent;
import bitcoinGWT.client.scatter_chart.ScatterChartComponent;
import bitcoinGWT.client.ticker.TickerComponent;
import bitcoinGWT.client.trades.TradesComponent;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.sencha.gxt.core.client.util.Margins;
import com.sencha.gxt.widget.core.client.container.*;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/2/13
 * Time: 10:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class MainContentPanel extends Viewport {

    private BitcoinGWTServiceAsync mainService;

    public MainContentPanel(BitcoinGWTServiceAsync mainService) {
        this.mainService = mainService;
        initComponents();
    }

    private void initComponents() {
        BorderLayoutContainer mainContainer = new BorderLayoutContainer();


        BorderLayoutContainer.BorderLayoutData northData = new BorderLayoutContainer.BorderLayoutData(62);
        northData.setCollapsible(true);
        mainContainer.setNorthWidget(new TickerComponent(mainService), northData);

        Margins margins = new Margins();
        margins.setBottom(45);

        BorderLayoutContainer.BorderLayoutData westData = new BorderLayoutContainer.BorderLayoutData(200);
        westData.setCollapsible(true);
        westData.setMargins(margins);
        mainContainer.setWestWidget(new ControlsComponent(), westData);

        BorderLayoutContainer.BorderLayoutData eastData = new BorderLayoutContainer.BorderLayoutData(400);
        eastData.setCollapsible(true);
        eastData.setMargins(margins);
        mainContainer.setEastWidget(new TradesComponent(mainService), eastData);

        addChartComponent2(mainContainer);
        //addChartComponent(mainContainer);
        //addScatterChart(mainContainer);

        setWidget(mainContainer);
    }

    private void addChartComponent(final BorderLayoutContainer mainContainer) {
        // Create a callback to be called when the visualization API
        // has been loaded.
        Runnable onLoadCallback = new Runnable() {
            public void run() {
                mainContainer.setCenterWidget(new ChartComponent());
            }
        };

        // Load the visualization api, passing the onLoadCallback to be called
        // when loading is done.
        VisualizationUtils.loadVisualizationApi(onLoadCallback, CandleStickChart.PACKAGE);
    }

    private void addChartComponent2(final BorderLayoutContainer mainContainer) {
        // Create the API Loader
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(new Runnable() {

            @Override
            public void run() {
                //getSimpleLayoutPanel().setWidget(getPieChart());
                //drawPieChart();
                mainContainer.setCenterWidget(new ChartComponent2());
            }
        });
    }

    private void addScatterChart(final BorderLayoutContainer mainContainer) {
        // Create the API Loader
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(new Runnable() {

            @Override
            public void run() {
                //getSimpleLayoutPanel().setWidget(getPieChart());
                //drawPieChart();
                mainContainer.setCenterWidget(new ScatterChartComponent());
            }
        });
    }


}
