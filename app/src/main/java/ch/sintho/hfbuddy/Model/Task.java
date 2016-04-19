package ch.sintho.hfbuddy.Model;

import android.content.ContentValues;
import android.graphics.Bitmap;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ch.sintho.hfbuddy.Data.Converter;
import ch.sintho.hfbuddy.Data.DbContext;
import ch.sintho.hfbuddy.Helpers.MediaHelper;

/**
 * Created by Sintho on 31.01.2016.
 */
public class Task implements Annotation, IBase {

    private int id;
    private Date date;
    private String title;
    private String note;

    private Bitmap thumbnail;
    private Bitmap picture;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    private Subject subject;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Bitmap getPicture() {
        return picture;
    }

    public void setPicture(Bitmap picture) {
        this.picture = picture;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(Bitmap thumbnail) {
        this.thumbnail = thumbnail;
    }


    @Override
    public Class<? extends Annotation> annotationType() {
        return Task.class;
    }

    @Override
    public void save() {
        ContentValues cv = new ContentValues();

        cv.put(DbContext.COLUMN_TITLE, getTitle());
        cv.put(DbContext.COLUMN_NOTE, getNote());

        Bitmap bm = getPicture();
        if (bm != null)
        {
            String picturepath = MediaHelper.saveToInternalStorage(bm, "img");
            cv.put(DbContext.COLUMN_IMAGE, picturepath);

            Bitmap th = getThumbnail();
            String thumbnailpath = MediaHelper.saveToInternalStorage(th, "thumbnail");
            cv.put(DbContext.COLUMN_THUMBNAIL, thumbnailpath);
        }

        cv.put(DbContext.COLUMN_SUBJECT_FK, getSubject() != null ? getSubject().getId(): null);

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        cv.put(DbContext.COLUMN_TASKDATE, df.format(getDate()));

        try {
            DbContext.getInstance().save(this, DbContext.TABLE_TASKS, cv);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }
}
