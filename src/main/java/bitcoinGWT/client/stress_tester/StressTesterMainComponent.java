package bitcoinGWT.client.stress_tester;

import bitcoinGWT.client.CustomAsyncCallback;
import bitcoinGWT.client.util.UiUtils;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.Markets;
import bitcoinGWT.shared.model.TickerFullLayoutObject;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
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
    private TextField tickerTestNumberOfTesters;
    private CheckBox tickerStressTestEnabled;

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

        HorizontalLayoutContainer tickerLoaderParams = new HorizontalLayoutContainer();
        tickerStressTestEnabled = new CheckBox();
        tickerStressTestEnabled.setBoxLabel("Enabled?");
        tickerLoaderParams.add(tickerStressTestEnabled, new HorizontalLayoutContainer.HorizontalLayoutData(-1, 20));

        tickerTestNumberOfTesters = new TextField();
        tickerTestNumberOfTesters.setEmptyText("Number of testers");
        tickerTestNumberOfTesters.setAllowBlank(false);
        tickerLoaderParams.add(tickerTestNumberOfTesters, new HorizontalLayoutContainer.HorizontalLayoutData(-1, 20));
        p.add(new FieldLabel(tickerLoaderParams, "Stress test ticker load"), new VerticalLayoutContainer.VerticalLayoutData(-1, 20));

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
                //start the test stop timer
                Timer testStopTimer = new Timer() {
                    @Override
                    public void run() {
                        //stop the test
                        executeTest = false;
                    }
                };
                testStopTimer.schedule(120 * 1000);

                if (gridStressTestEnabled.getValue()) {
                    Integer gridStressNumberOfTesters = Integer.parseInt(gridTestNumberOfTesters.getValue());
                    for (int i = 0; i < gridStressNumberOfTesters; i++) {
                        startTimer(new StressExecutor() {
                            @Override
                            public void execute(List<Currency> result) {
                                final StressExecutor current = this;
                                UiUtils.getAsyncService().getTradesForGrid(getMarket()
                                        , result.get(Random.nextInt(result.size())), null, new PagingLoadConfigBean(0, 200), new StressAsyncCallback<PagingLoadResult<TradesFullLayoutObject>>());
                            }
                        });
                    }
                }
                if (tickerStressTestEnabled.getValue()) {
                    Integer tickerStressNumberOfTesters = Integer.parseInt(tickerTestNumberOfTesters.getValue());
                    for (int i = 0; i < tickerStressNumberOfTesters; i++) {
                        startTimer(new StressExecutor() {
                            @Override
                            public void execute(List<Currency> result) {
                                UiUtils.getAsyncService().getPrice(getMarket(), result.get(Random.nextInt(result.size())), new StressAsyncCallback<TickerFullLayoutObject>());
                            }
                        });
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

    private void startTimer(final StressExecutor stressExecutor) {
        Timer timer = new Timer() {

            @Override
            public void run() {
                final Markets testMarket = Markets.values()[Random.nextInt(Markets.values().length)];
                stressExecutor.setMarket(testMarket);
                UiUtils.getAsyncService().getSupportedCurrencies(testMarket, new CustomAsyncCallback<List<Currency>>() {
                    @Override
                    public void onSuccess(List<Currency> result) {
                        stressExecutor.execute(result);
                    }
                });

            }
        };
        timer.schedule(Random.nextInt(20000));
    }

    private abstract class StressExecutor {

        private Markets market;

        public void setMarket(Markets market) {
            this.market = market;
        }

        public Markets getMarket() {
            return market;
        }

        public abstract void execute(List<Currency> result);

        class StressAsyncCallback<T> implements com.google.gwt.user.client.rpc.AsyncCallback<T> {


            public void onFailure(Throwable caught) {
                if (executeTest) {
                    startTimer(StressExecutor.this);
                }
            }

            public void onSuccess(T result) {
                startTimer(StressExecutor.this);
            }
        }


    }
}
