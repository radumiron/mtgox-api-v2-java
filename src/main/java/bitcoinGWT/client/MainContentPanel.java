package bitcoinGWT.client;

import bitcoinGWT.client.ticker.TickerComponent;
import com.google.gwt.user.client.ui.Button;
import com.sencha.gxt.widget.core.client.ContentPanel;
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

        setWidget(mainContainer);
    }


}
