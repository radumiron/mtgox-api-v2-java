
/**
 * Sencha GXT 3.0.1 - Sencha for GWT
 * Copyright(c) 2007-2012, Sencha, Inc.
 * licensing@sencha.com
 *
 * http://www.sencha.com/products/gxt/license/
 */
package bitcoinGWT.client;

import bitcoinGWT.shared.model.TradesFullLayoutObject;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.sencha.gxt.data.shared.loader.PagingLoadConfig;
import com.sencha.gxt.data.shared.loader.PagingLoadResult;
 
@RemoteServiceRelativePath("example")
public interface ExampleService extends RemoteService {
 
  PagingLoadResult<TradesFullLayoutObject> getPosts(PagingLoadConfig config, boolean initialLoad);

}
