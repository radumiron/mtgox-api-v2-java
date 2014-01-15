package bitcoinGWT.client.util;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 4/15/13
 * Time: 9:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MyImages extends ClientBundle {
    public static final MyImages INSTANCE = GWT.create(MyImages.class);

    @Source("images/bitcoin_charts.png")
    ImageResource bitcoinCharts();

    @Source("images/bitcurex2.png")
    ImageResource bitcurex();

    @Source("images/bitstamp2.png")
    ImageResource bitstamp();

    @Source("images/btcchina3.png")
    ImageResource btcchina();

    @Source("images/btce.png")
    ImageResource btce();

    @Source("images/campbx3.png")
    ImageResource campbx();

    @Source("images/cavirtex.png")
    ImageResource cavirtex();

    @Source("images/kraken2.png")
    ImageResource kraken();

    @Source("images/mtgox.png")
    ImageResource mtgox();
}
