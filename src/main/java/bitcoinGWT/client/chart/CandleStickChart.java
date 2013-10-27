package bitcoinGWT.client.chart;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/26/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Element;
import com.google.gwt.visualization.client.AbstractDataTable;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.Selection;
import com.google.gwt.visualization.client.events.Handler;
import com.google.gwt.visualization.client.events.ReadyHandler;
import com.google.gwt.visualization.client.events.SelectHandler;
import com.google.gwt.visualization.client.events.StateChangeHandler;
import com.google.gwt.visualization.client.visualizations.MotionChart;
import com.google.gwt.visualization.client.visualizations.Visualization;
import com.google.gwt.visualization.client.visualizations.corechart.PieChart;

/**
 * Motion Chart visualization. Note that this chart does not work when loading
 * the HTML from a local file. It works only when loading the HTML from a web
 * server.
 *
 * @see <a
 *      href="http://code.google.com/apis/visualization/documentation/gallery/motionchart.html"
 *      > Motion Chart Visualization Reference</a>
 */
public class CandleStickChart extends Visualization<CandleStickChartOptions> {

    public static final String PACKAGE = "corechart";

    public CandleStickChart() {
        super();
    }

    public CandleStickChart(AbstractDataTable data, CandleStickChartOptions candleStickChartOptions) {
        super(data, candleStickChartOptions);
    }

    public final void addReadyHandler(ReadyHandler handler) {
        Handler.addHandler(this, "ready", handler);
    }

    public final void addStateChangeHandler(StateChangeHandler handler) {
        Handler.addHandler(this, "statechange", handler);
    }

    /**
     * Returns the current state of the {@link MotionChart}, serialized to a JSON
     * string. To assign this state to the chart, assign this string to the state
     * option in the draw() method. This is often used to specify a custom chart
     * state on startup, instead of using the default state.
     *
     * @return a JSON encoded string indicating the state of the UI. This method
     *         may return <code>null</code> if the state was not supplied by
     *         {@link MotionChart.Options#setState(String)} or a statechange event
     *         has not yet fired.
     */
    public final native String getState() /*-{
        var jso = this.@com.google.gwt.visualization.client.visualizations.Visualization::getJso()();

        // The getState() method doesn't seem to always be present. I think this
        // happens when you don't properly initialize it or when you try to query
        // it before a statechanged event fires.
        if (jso.getState) {
            return jso.getState();
        }
        return null;
    }-*/;

    @Override
    protected native JavaScriptObject createJso(Element parent) /*-{
        return new $wnd.google.visualization.CandlestickChart(parent);
    }-*/;

    public final void addSelectHandler(SelectHandler handler) {
        Selection.addSelectHandler(this, handler);
    }

    public final JsArray<Selection> getSelections() {
        return Selection.getSelections(this);
    }

}

