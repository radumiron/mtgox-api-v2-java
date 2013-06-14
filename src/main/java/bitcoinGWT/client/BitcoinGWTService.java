package bitcoinGWT.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("BitcoinGWTService")
public interface BitcoinGWTService extends RemoteService {
    // Sample interface method of remote interface
    String getMessage(String msg);

    /**
     * Utility/Convenience class.
     * Use BitcoinGWTService.App.getInstance() to access static instance of BitcoinGWTServiceAsync
     */
    public static class App {
        private static BitcoinGWTServiceAsync ourInstance = GWT.create(BitcoinGWTService.class);

        public static synchronized BitcoinGWTServiceAsync getInstance() {
            return ourInstance;
        }
    }
}
