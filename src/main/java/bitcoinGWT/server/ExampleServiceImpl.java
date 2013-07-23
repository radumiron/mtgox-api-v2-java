package bitcoinGWT.server;

import bitcoinGWT.client.ExampleService;
import bitcoinGWT.server.ticker.TickerEngine;
import bitcoinGWT.server.ticker.TradesEngine;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.sencha.gxt.data.shared.SortDir;
import com.sencha.gxt.data.shared.SortInfo;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
import com.sencha.gxt.data.shared.loader.PagingLoadResultBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/11/13
 * Time: 12:09 AM
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ExampleServiceImpl extends RemoteServiceServlet implements ExampleService {

    @Autowired
    TradesEngine tradesEngine;

    @Autowired
    TickerEngine tickerEngine;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        WebApplicationContext ctx = WebApplicationContextUtils
                .getRequiredWebApplicationContext(config.getServletContext());
        AutowireCapableBeanFactory beanFactory = ctx
                .getAutowireCapableBeanFactory();
        beanFactory.autowireBean(this);
    }

    @Override
    public PagingLoadResult<TradesFullLayoutObject> getPosts(PagingLoadConfig config, boolean initialLoad) {
        Random random = new Random();
        int tradesSize = random.nextInt(2);

        List<TradesFullLayoutObject> trades = new ArrayList<>(tradesEngine.getTrades(Currency.EUR, initialLoad));

        for (SortInfo sortField : config.getSortInfo()) {
            Comparator<TradesFullLayoutObject> comparator = getComparator(sortField);
            if (comparator != null) { //in case we have a valid comparator for this field
                Collections.sort(trades, comparator);
            }
        }

        ArrayList<TradesFullLayoutObject> sublist = new ArrayList<TradesFullLayoutObject>();
        int start = config.getOffset();
        int limit = trades.size();
        if (config.getLimit() > 0) {
            limit = Math.min(start + config.getLimit(), limit);
        }

        for (int i = config.getOffset(); i < limit; i++) {
            sublist.add(trades.get(i));
        }

        return new PagingLoadResultBean<TradesFullLayoutObject>(sublist, trades.size(), config.getOffset());
    }

    private Comparator<TradesFullLayoutObject> getComparator(final SortInfo sortParams) {
        switch (sortParams.getSortField()) {
            case "price" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getPrice().compareTo(o2.getPrice()) : (-1) * o1.getPrice().compareTo(o2.getPrice());
                }
            };
            case "date" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getDate().compareTo(o2.getDate()) : (-1) * o1.getDate().compareTo(o2.getDate());
                }
            };
            case "amount" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getAmount().compareTo(o2.getAmount()) : (-1) * o1.getAmount().compareTo(o2.getAmount());
                }
            };
            case "tradeItem" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getTradeItem().compareTo(o2.getTradeItem()) : (-1) * o1.getTradeItem().compareTo(o2.getTradeItem());
                }
            };
            case "currency" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getCurrency().compareTo(o2.getCurrency()) : (-1) * o1.getCurrency().compareTo(o2.getCurrency());
                }
            };
            case "type" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getType().compareTo(o2.getType()) : (-1) * o1.getType().compareTo(o2.getType());
                }
            };

        }

        return null;
    }
}
