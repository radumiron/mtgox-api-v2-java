package bitcoinGWT.server;

import bitcoinGWT.server.ticker.TickerEngine;
import bitcoinGWT.server.ticker.TradesEngine;
import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TickerFullLayoutObject;
import bitcoinGWT.shared.model.TickerShallowObject;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import bitcoinGWT.client.BitcoinGWTService;
import mtgox_api.com.mtgox.api.ApiKeys;
import mtgox_api.com.mtgox.api.MtGox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

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
}