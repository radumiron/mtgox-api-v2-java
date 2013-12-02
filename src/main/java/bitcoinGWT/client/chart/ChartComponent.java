package bitcoinGWT.client.chart;

import bitcoinGWT.shared.model.Constants;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.sencha.gxt.widget.core.client.ContentPanel;

import java.util.Date;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/9/13
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChartComponent extends ContentPanel {

    private DataTable data;
    private CandleStickChart candleStickChart;

    public ChartComponent() {
        initComponents();
    }

    private void initComponents() {
        setExpanded(true);
        setCollapsible(true);
        setAnimCollapse(true);
        //setHeaderVisible(false);

        setHeaderVisible(true);
        setHeadingText("Chart");

        candleStickChart = new CandleStickChart(createTable(), createOptions());
        candleStickChart.addSelectHandler(createSelectHandler(candleStickChart));

        add(candleStickChart);

        startTimer();
    }

    private void startTimer() {
        Timer timer = new Timer() {

            //@Override
            public void run() {
                refreshChart();
            }
        };
        //timer.schedule(Constants.INITIAL_UI_TRADES_DELAY);
        timer.scheduleRepeating(Constants.TRADES_RETRIEVAL_INTERVAL);
    }

    private SelectHandler createSelectHandler(final CandleStickChart chart) {
        return new SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                String message = "";

                // May be multiple selections.
                JsArray<Selection> selections = chart.getSelections();

                for (int i = 0; i < selections.length(); i++) {
                    // add a new line for each selection
                    message += i == 0 ? "" : "\n";

                    Selection selection = selections.get(i);

                    if (selection.isCell()) {
                        // isCell() returns true if a cell has been selected.

                        // getRow() returns the row number of the selected cell.
                        int row = selection.getRow();
                        // getColumn() returns the column number of the selected cell.
                        int column = selection.getColumn();
                        message += "cell " + row + ":" + column + " selected";
                    } else if (selection.isRow()) {
                        // isRow() returns true if an entire row has been selected.

                        // getRow() returns the row number of the selected row.
                        int row = selection.getRow();
                        message += "row " + row + " selected";
                    } else {
                        // unreachable
                        message += "Pie chart selections should be either row selections or cell selections.";
                        message += "  Other visualizations support column selections as well.";
                    }
                }

                Window.alert(message);
            }
        };
    }

    public CandleStickChartOptions createOptions() {
        CandleStickChartOptions options = CandleStickChartOptions.create();
        options.setWidth(600);
        options.setHeight(540);
        //options.set3D(true);
        //options.setTitle("My Daily Activities");
        return options;
    }

    public AbstractDataTable createTable() {
        data = DataTable.create();
        data.addColumn(AbstractDataTable.ColumnType.STRING);
        data.addColumn(AbstractDataTable.ColumnType.NUMBER);
        data.addColumn(AbstractDataTable.ColumnType.NUMBER);
        data.addColumn(AbstractDataTable.ColumnType.NUMBER);
        data.addColumn(AbstractDataTable.ColumnType.NUMBER);

        data.addRows(2);
        //data.setValue(row_id, low, open, close, high)
        data.setValue(0, 0, "Mon");
        data.setValue(0, 1, 20);
        data.setValue(0, 2, 28);
        data.setValue(0, 3, 38);
        data.setValue(0, 4, 45);

        data.setValue(1, 0, "Tue");
        data.setValue(1, 1, 31);
        data.setValue(1, 2, 38);
        data.setValue(1, 3, 55);
        data.setValue(1, 4, 66);
        return data;
    }

    public void refreshChart(){
        /*data = DataTable.create();
        data.addColumn(AbstractDataTable.ColumnType.STRING);
        data.addColumn(AbstractDataTable.ColumnType.NUMBER);
        data.addColumn(AbstractDataTable.ColumnType.NUMBER);
        data.addColumn(AbstractDataTable.ColumnType.NUMBER);
        data.addColumn(AbstractDataTable.ColumnType.NUMBER);*/

        int originalRows = data.getNumberOfRows();
        data.addRows(1);

        Random random = new Random();
        //data.setValue(row_id, low, open, close, high)
        int high = random.nextInt(100);
        int low = random.nextInt(high);
        int open = low;
        do {
            open = random.nextInt(high);
        }
        while (open < low);

        int close = low;
        do {
            close = random.nextInt(high);
        }
        while (close < low);

        data.setValue(originalRows, 0, "Mon");
        data.setValue(originalRows, 1, low);
        data.setValue(originalRows, 2, open);
        data.setValue(originalRows, 3, close);
        data.setValue(originalRows, 4, high);

        candleStickChart.draw(data,createOptions());
    }

}
