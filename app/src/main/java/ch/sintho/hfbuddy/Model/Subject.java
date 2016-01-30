package ch.sintho.hfbuddy.Model;

import android.content.ContentValues;

import java.lang.annotation.Annotation;
import java.sql.SQLException;

import ch.sintho.hfbuddy.Data.DbContext;

/**
 * Created by Sintho on 08.01.2016.
 */
public class Subject implements Annotation, IBase{

    private String name = "";
    private int id = 0;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public Class<? extends Annotation> annotationType() {
        return this.getClass();
    }

    @Override
    public void save() {
        ContentValues cv = new ContentValues();
        cv.put(DbContext.COLUMN_NAME, getName());
        try {
            DbContext.getInstance().save(this, DbContext.TABLE_SUBJECTS, cv);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return getName();
    }
}
