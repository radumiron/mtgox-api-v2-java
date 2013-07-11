
/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package bitcoinGWT.shared.model.example;

import java.util.Date;

import bitcoinGWT.shared.model.Currency;
import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.ModelKeyProvider;
import com.sencha.gxt.data.shared.PropertyAccess;

public interface PostProperties extends PropertyAccess<TradesFullLayoutObject> {
    ValueProvider<TradesFullLayoutObject, Date> date();

    ValueProvider<TradesFullLayoutObject, Currency> currency();

    ValueProvider<TradesFullLayoutObject, Double> price();

    ValueProvider<TradesFullLayoutObject, Double> amount();

    ValueProvider<TradesFullLayoutObject, TradesFullLayoutObject.TradeType> type();

    ValueProvider<TradesFullLayoutObject, Currency> tradeItem();
}
