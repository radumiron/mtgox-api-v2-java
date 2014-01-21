package bitcoinGWT.client.stress_tester;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/21/14
 * Time: 12:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class StressTesterEntryPoint implements EntryPoint {
    @Override
    public void onModuleLoad() {
        StressTesterMainComponent viewport = new StressTesterMainComponent();
        RootPanel.get().add(viewport);
    }
}
