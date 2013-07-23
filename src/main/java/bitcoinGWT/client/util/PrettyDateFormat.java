package bitcoinGWT.client.util;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.shared.TimeZone;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Radu
 * Date: 7/15/13
 * Time: 10:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class PrettyDateFormat extends DateTimeFormat {
    private PrettyTime format = new PrettyTime();

    public PrettyDateFormat(String pattern) {
        super(pattern);
    }

    @Override
    public String format(Date date) {
        return format.format(date);
    }

}
