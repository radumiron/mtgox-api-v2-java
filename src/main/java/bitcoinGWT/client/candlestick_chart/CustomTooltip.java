package bitcoinGWT.client.candlestick_chart;

import com.googlecode.gwt.charts.client.options.Tooltip;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 11/2/13
 * Time: 10:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomTooltip extends Tooltip {
    protected CustomTooltip() {
    }

    public static CustomTooltip create() {
        return createObject().cast();
    }

    public final native void setHtml(boolean isHtml) /*-{
        this.isHtml = isHtml;
    }-*/;
}
