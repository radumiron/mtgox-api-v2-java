package bitcoinGWT.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BitcoinGWTServiceAsync {
    void getMessage(String msg, AsyncCallback<String> async);
}
