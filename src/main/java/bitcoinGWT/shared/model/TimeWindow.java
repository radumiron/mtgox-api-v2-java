package bitcoinGWT.shared.model;

import com.google.gwt.user.client.rpc.IsSerializable;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 10/29/13
 * Time: 9:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class TimeWindow implements IsSerializable {
    private Date start;
    private Date end;

    public TimeWindow(Date start, Date end) {
        this.end = end;
        this.start = start;
    }

    public TimeWindow() {
    }

    public Date getEnd() {
        return end;
    }

    public Date getStart() {
        return start;
    }

    @Override
    public String toString() {
        return "TimeWindow{" +
                "end=" + end +
                ", start=" + start +
                '}';
    }
}
