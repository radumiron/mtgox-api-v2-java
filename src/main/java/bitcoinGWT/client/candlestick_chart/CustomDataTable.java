package bitcoinGWT.client.candlestick_chart;

import com.googlecode.gwt.charts.client.DataTable;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 11/2/13
 * Time: 12:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class CustomDataTable extends DataTable {
    protected CustomDataTable() {
    }

    public static native CustomDataTable create() /*-{
        return new $wnd.google.visualization.DataTable();
    }-*/;

    public final native void addTooltipColumn(DataTable data, boolean isHtml) /*-{
        data.addColumn({'type': 'string', 'role': 'tooltip', 'p': {'html': isHtml}});
    }-*/;
}
