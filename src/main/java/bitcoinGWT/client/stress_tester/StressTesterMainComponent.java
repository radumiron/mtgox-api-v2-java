package bitcoinGWT.client.stress_tester;

import bitcoinGWT.client.CustomAsyncCallback;
import bitcoinGWT.client.util.UiUtils;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadConfigBean;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.widget.core.client.ContentPanel;
import com.sencha.gxt.widget.core.client.FramedPanel;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.container.HorizontalLayoutContainer;
import com.sencha.gxt.widget.core.client.container.VerticalLayoutContainer;
import com.sencha.gxt.widget.core.client.event.SelectEvent;
import com.sencha.gxt.widget.core.client.form.CheckBox;
import com.sencha.gxt.widget.core.client.form.FieldLabel;
import com.sencha.gxt.widget.core.client.form.FieldSet;
import com.sencha.gxt.widget.core.client.form.TextField;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 1/21/14
 * Time: 12:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class StressTesterMainComponent extends ContentPanel {

    private TextButton start;
    private TextButton end;
    private CheckBox gridStressTestEnabled;
    private TextField gridTestNumberOfTesters;

    private boolean executeTest = false;

    public StressTesterMainComponent() {
        initComponents();
        initListeners();
    }

    private void initComponents() {
        FramedPanel form2 = new FramedPanel();
        form2.setHeadingText("Simple Form with FieldSets");
        form2.setWidth(400);
        form2.setHeight(300);

        FieldSet fieldSet = new FieldSet();
        fieldSet.setHeadingText("Testers Information");
        fieldSet.setCollapsible(true);
        form2.add(fieldSet);

        VerticalLayoutContainer p = new VerticalLayoutContainer();
        fieldSet.add(p);

        HorizontalLayoutContainer gridLoaderParams = new HorizontalLayoutContainer();
        gridStressTestEnabled = new CheckBox();
        gridStressTestEnabled.setBoxLabel("Enabled?");
        gridLoaderParams.add(gridStressTestEnabled, new HorizontalLayoutContainer.HorizontalLayoutData(-1, 20));

        gridTestNumberOfTesters = new TextField();
        gridTestNumberOfTesters.setEmptyText("Number of testers");
        gridTestNumberOfTesters.setAllowBlank(false);
        gridLoaderParams.add(gridTestNumberOfTesters, new HorizontalLayoutContainer.HorizontalLayoutData(-1, 20));
        p.add(new FieldLabel(gridLoaderParams, "Stress test grid load"), new VerticalLayoutContainer.VerticalLayoutData(-1, 20));

        /*TextField lastName = new TextField();
        lastName.setAllowBlank(false);
        p.add(new FieldLabel(lastName, "Last Name"), new VerticalLayoutContainer.VerticalLayoutData(1, -1));

        TextField email = new TextField();
        email.setAllowBlank(false);
        p.add(new FieldLabel(email, "Email"), new VerticalLayoutContainer.VerticalLayoutData(1, -1));*/

        start = new TextButton("Start");
        end = new TextButton("End");
        form2.addButton(start);
        form2.addButton(end);

        setWidget(form2);
    }

    private void initListeners() {
        start.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {
                executeTest = true;
                if (gridStressTestEnabled.getValue()) {
                    //start the test stop timer
                    Timer testStopTimer = new Timer() {
                        @Override
                        public void run() {
                            //stop the test
                            executeTest = false;
                        }
                    };
                    testStopTimer.schedule(120 * 1000);


                    Integer gridStressNumberOfTesters = Integer.parseInt(gridTestNumberOfTesters.getValue());
                    for (int i = 0; i < gridStressNumberOfTesters; i++) {
                        startTimer();
                    }
                }

            }
        });

        end.addSelectHandler(new SelectEvent.SelectHandler() {
            @Override
            public void onSelect(SelectEvent event) {

            }
        });
    }

    private void startTimer() {
        Timer timer = new Timer() {

            @Override
            public void run() {
                final Markets testMarket = Markets.values()[Random.nextInt(Markets.values().length)];
                UiUtils.getAsyncService().getSupportedCurrencies(testMarket, new CustomAsyncCallback<List<Currency>>() {
                    @Override
                    public void onSuccess(List<Currency> result) {
                        UiUtils.getAsyncService().getTradesForGrid(testMarket
                                , result.get(Random.nextInt(result.size())), null, new PagingLoadConfigBean(0, 200), new CustomAsyncCallback<PagingLoadResult<TradesFullLayoutObject>>() {
                            @Override
                            public void onFailure(Throwable caught) {
                                if (executeTest) {
                                    startTimer();
                                }
                            }

                            @Override
                            public void onSuccess(PagingLoadResult<TradesFullLayoutObject> result) {
                                if (executeTest) {
                                    startTimer();
                                }
                            }
                        });
                    }
                });

            }
        };
        timer.schedule(Random.nextInt(20000));
    }


}
