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
    HALF_HOUR(30),
    ONE_HOUR(60),
    THREE_HOURS(3 * 60),
    SIX_HOURS(6 * 60),
    ONE_DAY(24 * 60);

    private int minutes;

    private TimeInterval() {
    }

    TimeInterval(int minutes) {
        this.minutes = minutes;
    }

    public int getMinutes() {
        return minutes;
    }

    public int getSeconds() {
        return minutes * 60;
    }

    public Long getMilliseconds() {
        return new Long(minutes * getSeconds() * 1000);
    }

}
