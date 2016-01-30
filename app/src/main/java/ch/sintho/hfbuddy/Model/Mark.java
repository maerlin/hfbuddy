package ch.sintho.hfbuddy.Model;

import android.content.ContentValues;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ch.sintho.hfbuddy.Data.DbContext;

/**
 * Created by Sintho on 08.01.2016.
 */
public class Mark implements Annotation, IBase {

    private int mark = 0;
    private boolean lastPartialMark = false;
    private Subject subject;
    private PartialMarkType partialmarktype;
    private int id = 0;
    private Date date;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getLastPartialMark() {
        return lastPartialMark;
    }

    public void setLastPartialMark(boolean lastPartialMark) {
        this.lastPartialMark = lastPartialMark;
    }

    public void setMark(Double m) {
        m *= 1000;
        mark = m.intValue();
    }

    public double getMark() {
        return mark;
    }

    public String getMarkFormatted() {

        double d = 0.00;
        d = ((double) mark / 1000);
        return formatMark(d);
    }

    public static String formatMark(double d)
    {
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.GERMAN);
        otherSymbols.setDecimalSeparator('.');
        otherSymbols.setGroupingSeparator(',');
        DecimalFormat df = new DecimalFormat("0.00", otherSymbols);
        return df.format(d);
    }

    public PartialMarkType getPartialmarktype() {
        return partialmarktype;
    }

    public void setPartialmarktype(PartialMarkType partialmarktype) {
        this.partialmarktype = partialmarktype;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }


    @Override
    public Class<? extends Annotation> annotationType() {
        return this.getClass();
    }

    @Override
    public void save() {
        ContentValues cv = new ContentValues();

        cv.put(DbContext.COLUMN_LASTPARTIALMARK, getLastPartialMark() ? 1 : 0);
        cv.put(DbContext.COLUMN_MARK, getMark());
        cv.put(DbContext.COLUMN_SUBJECT_FK, getSubject().getId());

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        cv.put(DbContext.COLUMN_DATE, df.format(getDate()));
        PartialMarkType type = getPartialmarktype();
        cv.put(DbContext.COLUMN_PARTIALMARK_TYPE_FK, type != null ? type.getId() : null);

        try {
            DbContext.getInstance().save(this, DbContext.TABLE_MARKS, cv);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
