package ch.sintho.hfbuddy.Data;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteTransactionListener;
import android.os.Debug;
import android.util.Log;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.util.ArrayList;

import ch.sintho.hfbuddy.Model.IBase;
import ch.sintho.hfbuddy.Model.Mark;
import ch.sintho.hfbuddy.Model.PartialMarkType;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.Model.Task;

/**
 * Created by Sintho on 06.01.2016.
 */
public class DbContext extends SQLiteOpenHelper {

    private static final int DB_VERSION = 4;
    private static final String DB_NAME = "Marks.db";

    //Common Columns
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_NAME = "NAME";

    //Table Marks
    public static final String TABLE_MARKS = "MARKS";
    public static final String COLUMN_MARK = "MARK";
    public static final String COLUMN_LASTPARTIALMARK = "LASTPARTIALMARK";
    public static final String COLUMN_PARTIALMARK_TYPE_FK = "PARTIALMARK_TYPE_FK";
    public static final String COLUMN_SUBJECT_FK = "SUBJECT_FK";
    public static final String COLUMN_DATE = "MARKDATE";

    //Table Partialmark types
    public static final String TABLE_PARTIALMARK_TYPES = "PARTIALMARK_TYPES";

    //Table Subjects
    public static final String TABLE_SUBJECTS = "SUBJECTS";

    //Table Tasks
    public static final String TABLE_TASKS = "TASKS";
    public static final String COLUMN_TITLE = "TITLE";
    public static final String COLUMN_NOTE = "NOTE";
    public static final String COLUMN_TASKDATE = "TASKDATE";
    public static final String COLUMN_IMAGE = "IMAGEPATH";
    public static final String COLUMN_THUMBNAIL = "THUMBNAILPATH";

    private static final String CREATE_TABLE_MARKS = "CREATE TABLE "+ TABLE_MARKS + "("
                                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                                + COLUMN_MARK + " INTEGER,"
                                + COLUMN_LASTPARTIALMARK + " INTEGER,"
                                + COLUMN_PARTIALMARK_TYPE_FK + " INTEGER,"
                                + COLUMN_DATE + " DATE,"
                                + COLUMN_SUBJECT_FK + " INTEGER )";

    private static final String CREATE_TABLE_SUBJECTS = "CREATE TABLE "+ TABLE_SUBJECTS + "("
                                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                                + COLUMN_NAME + " TEXT)";

    private static final String CREATE_TABLE_PARTIALMARK_TYPES = "CREATE TABLE "+ TABLE_PARTIALMARK_TYPES + "("
                                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                                + COLUMN_NAME + " TEXT)";

    private static final String CREATE_TABLE_TASKS = "CREATE TABLE "+ TABLE_TASKS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY,"
            + COLUMN_TITLE + " TEXT,"
            + COLUMN_NOTE + " TEXT,"
            + COLUMN_TASKDATE + " DATE,"
            + COLUMN_IMAGE + " TEXT,"
            + COLUMN_THUMBNAIL + " TEXT,"
            + COLUMN_SUBJECT_FK + " INT)";

    private static DbContext instance;
    private static Context mCtx;

    public static DbContext getInstance()    {
        return getInstance(mCtx);
    }
    public static DbContext getInstance(Context context){
        if (context != null && mCtx != context)
            mCtx = context;

        if (instance == null)
            instance = new DbContext(context.getApplicationContext(), DB_NAME, null, DB_VERSION);

        return instance;
    }

    private SQLiteDatabase getDb() {
        return getWritableDatabase();
    }


    public void save(IBase obj, String table, ContentValues values) throws SQLException {

        SQLiteDatabase db = getDb();

        int id = obj.getId();
        if (id == 0) {
            id = (int)(long) db.insertOrThrow(table,null,values);
            obj.setId(id);
        }
        else {
            db.update(table,values,COLUMN_ID + "="+id, null);
        }

        db.close();
    }

    public <T extends Annotation> T getObjectById(String table, int id, Class<T> annotationType)    {
        ArrayList<T> list = executeQuery("SELECT * FROM " + table + " WHERE " + COLUMN_ID + " = " + id, annotationType, true);
        if (list == null || list.size() == 0)
            return null;

        return list.get(0);

    }

    public <T extends Annotation> ArrayList<T> executeQuery (String sql, Class<T> annotationType) {
        return executeQuery(sql, annotationType, false);
    }

    public String getStringColumnEntry(String sql)
    {
        SQLiteDatabase db = getDb();
        Cursor cursor = db.rawQuery(sql, null);

        if (!cursor.moveToFirst())
        {
            cursor.close();
            db.close();
            return null;
        }
        String result = cursor.getString(0);

        cursor.close();
        db.close();

        return result;

    }
    public <T extends Annotation> ArrayList<T> executeQuery (String sql, Class<T> annotationType, boolean loadfully) {

        SQLiteDatabase db = getDb();
        Cursor cursor = db.rawQuery(sql,null);

        if (!cursor.moveToFirst())
        {
            cursor.close();
            db.close();
            return null;
        }

        ArrayList<T> list = new ArrayList<T>();

        while (!cursor.isAfterLast()) {

            if (annotationType == Mark.class) {
                list.add((T)Converter.convertMarkFromDb(cursor));
            }
            else if (annotationType == PartialMarkType.class) {
                list.add((T)Converter.convertPartialMarkTypeFromDb(cursor));
            }
            else if (annotationType == Subject.class) {
                list.add((T)Converter.convertSubjectFromDb(cursor));
            }
            else if (annotationType == Task.class) {
                list.add((T)Converter.convertTaskFromDb(cursor,loadfully));
            }
            else
                throw new TypeNotPresentException(annotationType.getName(), null);

            cursor.moveToNext();
        }
        cursor.close();
        db.close();

        return list;
    }


    public DbContext(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, factory, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //CREATE TABLES
        db.execSQL(CREATE_TABLE_MARKS);
        db.execSQL(CREATE_TABLE_PARTIALMARK_TYPES);
        db.execSQL(CREATE_TABLE_SUBJECTS);
        db.execSQL(CREATE_TABLE_TASKS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}
