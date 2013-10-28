package bitcoinGWT.client;

import bitcoinGWT.client.util.UiUtils;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.DOM;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * Entry point classes define <code>onModuleLoad()</code>
 */
public class BitcoinGWT implements EntryPoint {
    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        MainContentPanel viewport = new MainContentPanel(UiUtils.getAsyncService());
        RootPanel.get().add(viewport);
    }
}
