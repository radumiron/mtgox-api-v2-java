package bitcoinGWT.client;

import bitcoinGWT.client.chart.ChartComponent;
import bitcoinGWT.client.controls.ControlsComponent;
import bitcoinGWT.client.ticker.TickerComponent;
import bitcoinGWT.client.trades.TradesComponent;
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

        BorderLayoutContainer.BorderLayoutData westData = new BorderLayoutContainer.BorderLayoutData(200);
        westData.setCollapsible(true);
        mainContainer.setWestWidget(new ControlsComponent(), westData);

        BorderLayoutContainer.BorderLayoutData eastData = new BorderLayoutContainer.BorderLayoutData(300);
        eastData.setCollapsible(true);
        mainContainer.setEastWidget(new TradesComponent(), eastData);

        mainContainer.setCenterWidget(new ChartComponent());

        setWidget(mainContainer);
    }


}
