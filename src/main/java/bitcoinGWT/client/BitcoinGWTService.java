package bitcoinGWT.client;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TickerFullLayoutObject;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;

@RemoteServiceRelativePath("bit")
public interface BitcoinGWTService extends RemoteService {
    // Sample interface method of remote interface
    String getMessage(String msg);


    TickerFullLayoutObject getPrice(Currency currency);

    PagingLoadResult<TradesFullLayoutObject> getTradesForGrid(Currency currency, PagingLoadConfig config, boolean initialLoad);

    int getLastLoadedTradesSizeFromServer(Currency currency);
}
