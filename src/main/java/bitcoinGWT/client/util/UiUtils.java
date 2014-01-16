package bitcoinGWT.client.util;

import bitcoinGWT.client.BitcoinGWTService;
import bitcoinGWT.client.BitcoinGWTServiceAsync;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/27/13
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class UiUtils {

    public static EventBus EVENT_BUS = GWT.create(SimpleEventBus.class);

    private static BitcoinGWTServiceAsync serviceAsync;

    public static BitcoinGWTServiceAsync getAsyncService() {
        /**
         * Create a remote service proxy to talk to the server-side Greeting service.
         */
        if (serviceAsync == null) {
            serviceAsync = GWT.create(BitcoinGWTService.class);
        }

        return serviceAsync;
    }

    public static ImageResource getImageForMarket(Markets market) {
        switch (market) {
            /*case BITCOINCHARTS:
                return MyImages.INSTANCE.bitcoinCharts();
            case BITCUREX:
                return MyImages.INSTANCE.bitcurex();
            case BITSTAMP:
                return MyImages.INSTANCE.bitstamp();*/
            /*case BLOCKCHAIN:
                return MyImages.INSTANCE.blockchain();*/
            /*case BTCCHINA:
                return MyImages.INSTANCE.btcchina();*/
            case BTCE:
                return MyImages.INSTANCE.btce();
            /*case CAMPBX:
                return MyImages.INSTANCE.campbx();*/
            /*case CAVIRTEX:
                return MyImages.INSTANCE.cavirtex();*/
           /* case KRAKEN:
                return MyImages.INSTANCE.kraken();*/
            case MTGOX: default:
                return MyImages.INSTANCE.mtgox();
        }
    }
}
