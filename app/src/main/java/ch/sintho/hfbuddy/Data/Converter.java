package ch.sintho.hfbuddy.Data;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import ch.sintho.hfbuddy.Helpers.MediaHelper;
import ch.sintho.hfbuddy.Model.Mark;
import ch.sintho.hfbuddy.Model.PartialMarkType;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.Model.Task;

/**
 * Created by Sintho on 08.01.2016.
 */
public class Converter {

    public static Mark getMarkById(int id)
    {
        Object result = DbContext.getInstance().getObjectById(DbContext.TABLE_MARKS, id, Mark.class);
        return result != null ? (Mark) result : null;
    }

    public static PartialMarkType getPartialMarkTypeById(int id)
    {
        Object result = DbContext.getInstance().getObjectById(DbContext.TABLE_PARTIALMARK_TYPES, id, PartialMarkType.class);
        return result != null ? (PartialMarkType) result : null;
    }

    public static Bitmap getBitmapFromByte(byte[] bytes)
    {
        ByteArrayInputStream imageStream = new ByteArrayInputStream(bytes);
        return BitmapFactory.decodeStream(imageStream);
    }

    public static byte[] getBytesFromBitmap(Bitmap bmp)
    {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }

    public static Subject getSubjectById(int id)
    {
        Object result = DbContext.getInstance().getObjectById(DbContext.TABLE_SUBJECTS, id, Subject.class);
        return result != null ? (Subject) result : null;
    }

    public static Task convertTaskFromDb(Cursor cursor, boolean loadfully) {

        Task task = new Task();
        task.setId(cursor.getInt(0));
        task.setTitle(cursor.getString(1));
        task.setNote(cursor.getString(2));
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        try {
            java.util.Date result = df.parse(cursor.getString(3));
            task.setDate(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String imgPath = cursor.getString(4);
        if (imgPath != null && !imgPath.isEmpty() && loadfully)
        {
            Bitmap img = MediaHelper.getImageFromStorage(imgPath);
            if (img != null)
                task.setPicture(img);
        }

        String thumbPath = cursor.getString(5);
        if (thumbPath != null && !thumbPath.isEmpty() && loadfully)
        {
            Bitmap th = MediaHelper.getImageFromStorage(thumbPath);
            if (th != null)
                task.setThumbnail(th);
        }

        int subid = cursor.getInt(6);

        if (subid > 0)
        {
            Subject sub = getSubjectById(subid);
            task.setSubject(sub);
        }

        return task;
    }
    public static Mark convertMarkFromDb(Cursor cursor) throws NullPointerException {
        Mark mark = new Mark();
        mark.setId(cursor.getInt(0));
        mark.setMark((double) cursor.getInt(1) / 1000);
        mark.setLastPartialMark(cursor.getInt(2) > 0);

        PartialMarkType type = getPartialMarkTypeById(cursor.getInt(3));
        mark.setPartialmarktype(type);

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

        try {
            java.util.Date result = df.parse(cursor.getString(4));
            mark.setDate(result);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Subject sub = getSubjectById(cursor.getInt(5));
        if (sub == null)
            throw new NullPointerException("PartialMarkType is NULL!");

        mark.setSubject(sub);

        return mark;
    }

    public static Subject convertSubjectFromDb(Cursor cursor)
    {
        Subject subject = new Subject();
        subject.setId(cursor.getInt(0));
        subject.setName(cursor.getString(1));
        return subject;
    }

    public static PartialMarkType convertPartialMarkTypeFromDb(Cursor cursor)
    {
        PartialMarkType partialMarkType = new PartialMarkType();
        partialMarkType.setId(cursor.getInt(0));
        partialMarkType.setName(cursor.getString(1));
        return partialMarkType;
    }




}
