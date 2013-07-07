package bitcoinGWT.client;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TickerFullLayoutObject;
import bitcoinGWT.shared.model.TickerShallowObject;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import mtgox_api.com.mtgox.api.MtGox;

@RemoteServiceRelativePath("bit")
public interface BitcoinGWTService extends RemoteService {
    // Sample interface method of remote interface
    String getMessage(String msg);


    TickerFullLayoutObject getPrice(Currency currency);
}
