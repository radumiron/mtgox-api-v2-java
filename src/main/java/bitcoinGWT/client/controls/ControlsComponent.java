package bitcoinGWT.client.controls;

import com.sencha.gxt.widget.core.client.ContentPanel;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/9/13
 * Time: 11:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ControlsComponent extends ContentPanel {

    public ControlsComponent() {
        initComponents();
    }

    private void initComponents() {
        setExpanded(true);
        setCollapsible(true);
        setAnimCollapse(true);
        setTitleCollapse(true);

        setHeadingText("Chart controls");
    }
}
