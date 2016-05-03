package ch.sintho.hfbuddy.Helpers;

import android.text.format.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.ocpsoft.pretty.time.PrettyTime;
import org.ocpsoft.pretty.time.TimeUnit;
import org.ocpsoft.pretty.time.units.Day;
import org.ocpsoft.pretty.time.units.Hour;
import org.ocpsoft.pretty.time.units.JustNow;
import org.ocpsoft.pretty.time.units.Millisecond;
import org.ocpsoft.pretty.time.units.Minute;
import org.ocpsoft.pretty.time.units.Second;

/**
 * Created by Sintho on 03.05.2016.
 */
public class DateHelper {

    public static boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();

        cal1.setTime(date1);
        cal2.setTime(date2);

        if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
        {
            if (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH))
            {
                if (cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH))
                {
                    return true;
                }
            }
        }

        return false;
    }

    private static  boolean checkNextDay(Date date, Calendar cal)
    {
        cal.add(Calendar.DATE,1);
        if (isSameDay(date, cal.getTime()))
            return true;

        return false;
    }

    public static String getReadableDatestring(Date date)
    {

        PrettyTime p = new PrettyTime();
        List<TimeUnit> units = new ArrayList<TimeUnit>();

        for( TimeUnit t : p.getUnits() ) {
            if(!(t instanceof JustNow) && !(t instanceof Hour) && !(t instanceof Minute) && !(t instanceof Second) && !(t instanceof Millisecond)) {
                units.add(t);
            }
        }
        p.setLocale(Locale.GERMAN);
        p.setUnits(units);
        return p.format(date);
    }
}
