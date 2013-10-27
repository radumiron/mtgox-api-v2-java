package bitcoinGWT.client;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/27/13
 * Time: 9:49 AM
 * To change this template use File | Settings | File Templates.
 */
import bitcoinGWT.client.chart2.ChartRangeFilterExample;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SimpleLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.charts.client.ChartLoader;
import com.googlecode.gwt.charts.client.ChartPackage;
import com.googlecode.gwt.charts.client.ColumnType;
import com.googlecode.gwt.charts.client.DataTable;
import com.googlecode.gwt.charts.client.corechart.PieChart;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class ChartsDemo2 implements EntryPoint {
    private SimpleLayoutPanel layoutPanel;
    private PieChart pieChart;

    /**
     * This is the entry point method.
     */
    @Override
    public void onModuleLoad() {
        Window.enableScrolling(false);
        Window.setMargin("0px");

        RootLayoutPanel.get().add(getSimpleLayoutPanel());

        // Create the API Loader
        ChartLoader chartLoader = new ChartLoader(ChartPackage.CORECHART);
        chartLoader.loadApi(new Runnable() {

            @Override
            public void run() {
                //getSimpleLayoutPanel().setWidget(getPieChart());
                //drawPieChart();
                getSimpleLayoutPanel().setWidget(new ChartRangeFilterExample());
            }
        });
    }

    private SimpleLayoutPanel getSimpleLayoutPanel() {
        if (layoutPanel == null) {
            layoutPanel = new SimpleLayoutPanel();
        }
        return layoutPanel;
    }

    private Widget getPieChart() {
        if (pieChart == null) {
            pieChart = new PieChart();
        }
        return pieChart;
    }

    private void drawPieChart() {
        // Prepare the data
        DataTable dataTable = DataTable.create();
        dataTable.addColumn(ColumnType.STRING, "Name");
        dataTable.addColumn(ColumnType.NUMBER, "Donuts eaten");
        dataTable.addRows(4);
        dataTable.setValue(0, 0, "Michael");
        dataTable.setValue(1, 0, "Elisa");
        dataTable.setValue(2, 0, "Robert");
        dataTable.setValue(3, 0, "John");
        dataTable.setValue(0, 1, 5);
        dataTable.setValue(1, 1, 7);
        dataTable.setValue(2, 1, 3);
        dataTable.setValue(3, 1, 2);

        // Draw the chart
        pieChart.draw(dataTable);
    }
}
