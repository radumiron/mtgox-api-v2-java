package bitcoinGWT.client;

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
     * Create a remote service proxy to talk to the server-side Greeting service.
     */
    private final BitcoinGWTServiceAsync mainService = GWT
            .create(BitcoinGWTService.class);

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        mainService.getMessage("client message", new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onSuccess(String result) {
                System.out.println("all ok, message from server:"+ result);
            }
        });

        MainContentPanel viewport = new MainContentPanel(mainService);
        RootPanel.get().add(viewport);
    }
}
