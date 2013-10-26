package bitcoinGWT.client.chart;

import com.sencha.gxt.widget.core.client.ContentPanel;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/9/13
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class ChartComponent extends ContentPanel {

    public ChartComponent() {
        initComponents();
    }

    private void initComponents() {
        setExpanded(true);
        setCollapsible(true);
        setAnimCollapse(true);
        setHeaderVisible(false);


        add(new CandleStickChart());
    }
}
