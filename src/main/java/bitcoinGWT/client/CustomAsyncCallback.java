package bitcoinGWT.client;

/*import org.apache.log4j.Logger;*/

import com.allen_sauer.gwt.log.client.Log;

public class CustomAsyncCallback<T> implements com.google.gwt.user.client.rpc.AsyncCallback<T> {
	

	public void onFailure(Throwable caught) {
        caught.printStackTrace();
        //Log.error("Error occurred during async call", caught);
	}

	public void onSuccess(T result) {
		onFailure(new UnsupportedOperationException("Each custom asynchronous call must override the onSuccess method"));
	}

}
