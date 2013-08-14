package bitcoinGWT.server;

import bitcoinGWT.server.ticker.TickerEngine;
import bitcoinGWT.server.ticker.TradesEngine;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TickerFullLayoutObject;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import bitcoinGWT.client.BitcoinGWTService;
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

@Service
public class BitcoinGWTServiceImpl extends RemoteServiceServlet implements BitcoinGWTService {

    @Autowired
    TickerEngine ticker;

    @Autowired
    TradesEngine tradesEngine;

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
    public String getMessage(String msg) {
        return "Client said: \"" + msg + "\"<br>Server answered: \"Hi!\"";
    }

    @Override
    public TickerFullLayoutObject getPrice(Currency currency) {
        return ticker.getPrice(currency);
    }

    @Override
    public PagingLoadResult<TradesFullLayoutObject> getTradesForGrid(Currency currency, PagingLoadConfig config, boolean initialLoad) {
        //always take the whole list of retrieved trades: we will return to the UI just part of it, according to the PagingLoadConfig
        List<TradesFullLayoutObject> trades = new ArrayList<>(tradesEngine.getTrades(Currency.EUR, true));

        for (SortInfo sortField : config.getSortInfo()) {
            Comparator<TradesFullLayoutObject> comparator = getComparator(sortField);
            if (comparator != null) { //in case we have a valid comparator for this field
                Collections.sort(trades, comparator);
            }
        }

        //create the sublist to return
        ArrayList<TradesFullLayoutObject> sublist = new ArrayList<TradesFullLayoutObject>();
        int start = config.getOffset(); //take the offset as start point
        int limit = trades.size();      //set the trades size as upper limit
        if (config.getLimit() > 0) {    //in case we want to retrieve part of the result
            limit = Math.min(start + config.getLimit(), limit); //try to take as many as possible, within the limits of the trades list
        }

        for (int i = config.getOffset(); i < limit; i++) {  //put the paged trades inside the sublist
            sublist.add(trades.get(i));
        }

        //return the paged trades, also sending the no. of total results (trades.size) and the offset from the start point of the list for which the sublist corresponds.
        return new PagingLoadResultBean<TradesFullLayoutObject>(sublist, trades.size(), config.getOffset());
    }

    @Override
    public int getLastLoadedTradesSizeFromServer(Currency currency) {
        return tradesEngine.getLastLoadedTradesSizeFromServer(currency);
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
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getPrice().compareTo(o2.getPrice()) : (-1) * o1.getPrice().compareTo(o2.getPrice());
                }
            };
            case "amount" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getPrice().compareTo(o2.getPrice()) : (-1) * o1.getPrice().compareTo(o2.getPrice());
                }
            };
            case "tradeItem" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getPrice().compareTo(o2.getPrice()) : (-1) * o1.getPrice().compareTo(o2.getPrice());
                }
            };
            case "currency" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getPrice().compareTo(o2.getPrice()) : (-1) * o1.getPrice().compareTo(o2.getPrice());
                }
            };
            case "type" : return new Comparator<TradesFullLayoutObject>() {

                @Override
                public int compare(TradesFullLayoutObject o1, TradesFullLayoutObject o2) {
                    return sortParams.getSortDir() == SortDir.ASC ? o1.getPrice().compareTo(o2.getPrice()) : (-1) * o1.getPrice().compareTo(o2.getPrice());
                }
            };

        }

        return null;
    }



}