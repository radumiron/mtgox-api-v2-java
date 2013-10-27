package bitcoinGWT.client.chart;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.visualization.client.AbstractDrawOptions;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/26/13
 * Time: 12:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CandleStickChartOptions extends AbstractDrawOptions {
    public static CandleStickChartOptions create() {
        return JavaScriptObject.createObject().cast();
    }

    protected CandleStickChartOptions() {
    }

    public final native void setHeight(int height) /*-{
        this.height = height;
    }-*/;

    public final native void setShowAdvancedPanel(boolean showAdvancedPanel) /*-{
        this.showAdvancedPanel = showAdvancedPanel;
    }-*/;

    public final native void setShowChartButtons(boolean showChartButtons) /*-{
        this.showChartButtons = showChartButtons;
    }-*/;

    public final native void setShowHeader(boolean showHeader) /*-{
        this.showHeader = showHeader;
    }-*/;

    public final native void setShowSelectListComponent(
            boolean showSelectListComponent) /*-{
        this.showSelectListComponent = showSelectListComponent;
    }-*/;

    public final native void setShowSidePanel(boolean showSidePanel) /*-{
        this.showSidePanel = showSidePanel;
    }-*/;

    public final native void setShowXMetricPicker(boolean showXMetricPicker) /*-{
        this.showXMetricPicker = showXMetricPicker;
    }-*/;

    public final native void setShowXScalePicker(boolean showXScalePicker) /*-{
        this.showXScalePicker = showXScalePicker;
    }-*/;

    public final native void setShowYMetricPicker(boolean showYMetricPicker) /*-{
        this.showYMetricPicker = showYMetricPicker;
    }-*/;

    public final native void setShowYScalePicker(boolean showYScalePicker) /*-{
        this.showYScalePicker = showYScalePicker;
    }-*/;

    public final void setSize(int width, int height) {
        setWidth(width);
        setHeight(height);
    }

    public final native void setState(String state) /*-{
        this.state = state;
    }-*/;

    public final native void setWidth(int width) /*-{
        this.width = width;
    }-*/;
}
