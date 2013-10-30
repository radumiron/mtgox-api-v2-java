package bitcoinGWT.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/27/13
 * Time: 8:40 PM
 * To change this template use File | Settings | File Templates.
 */
public enum TimeInterval implements IsSerializable{

    TEN_MINUTES(10),
    ONE_HOUR(60);

    private int minutes;

    private TimeInterval() {
    }

    TimeInterval(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

}
