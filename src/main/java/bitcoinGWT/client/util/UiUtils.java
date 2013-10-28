package bitcoinGWT.client.util;

import bitcoinGWT.client.BitcoinGWTService;
import bitcoinGWT.client.BitcoinGWTServiceAsync;
import com.google.gwt.core.client.GWT;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/27/13
 * Time: 12:50 PM
 * To change this template use File | Settings | File Templates.
 */
public class UiUtils {

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
}
