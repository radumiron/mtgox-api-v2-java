package bitcoinGWT.client.trades;

import com.sencha.gxt.widget.core.client.ContentPanel;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/9/13
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class TradesComponent extends ContentPanel {
    public TradesComponent() {
        initComponent();
    }

    private void initComponent() {
        setExpanded(true);
        setCollapsible(true);
        setAnimCollapse(true);
        setTitleCollapse(true);

        setHeadingText("Trades");
    }
}
