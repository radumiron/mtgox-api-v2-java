package bitcoinGWT.server.ticker.factory;

import bitcoinGWT.server.ticker.AbstractTradeEngine;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 2/8/14
 * Time: 10:11 AM
 * To change this template use File | Settings | File Templates.
 */
public class GenericFactoryBean implements FactoryBean<AbstractTradeEngine> {

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public AbstractTradeEngine getObject() throws Exception {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Class<? extends AbstractTradeEngine> getObjectType() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isSingleton() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
