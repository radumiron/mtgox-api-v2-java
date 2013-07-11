package bitcoinGWT.server;

import bitcoinGWT.client.ExampleService;
import bitcoinGWT.server.ticker.TickerEngine;
import bitcoinGWT.server.ticker.TradesEngine;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public PagingLoadResult<TradesFullLayoutObject> getPosts(PagingLoadConfig config) {
        Random random = new Random();
        int tradesSize = random.nextInt(2);

        List<TradesFullLayoutObject> trades = new ArrayList<>(tradesEngine.getTrades(Currency.EUR, 0));

        PagingLoadResult<TradesFullLayoutObject> pagingLoadResult = new PagingLoadResultBean<>(trades, trades.size(), config.getOffset());
        return pagingLoadResult;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
