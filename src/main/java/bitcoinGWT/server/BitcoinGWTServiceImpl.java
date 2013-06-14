package bitcoinGWT.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import bitcoinGWT.client.BitcoinGWTService;

public class BitcoinGWTServiceImpl extends RemoteServiceServlet implements BitcoinGWTService {
    // Implementation of sample interface method
    public String getMessage(String msg) {
        return "Client said: \"" + msg + "\"<br>Server answered: \"Hi!\"";
    }
}