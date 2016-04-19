package ch.sintho.hfbuddy.Helpers;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.widget.ImageButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Sintho on 17.04.2016.
 */
public class MediaHelper {

    private static ContextWrapper cw;

    public static String GetAbsolutePath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        Log.i("URI",uri+"");
        String result = uri+"";

        if (isKitKat && (result.contains("media.documents"))) {
            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length-1];
            final String[] dat = imgary.split("%3A");

            final String type = dat[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            }
            final String selection = "_id=?";
            final String[] selectionArgs = new String[] { dat[1] };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static void deleteFile(String path)
    {
        if (path == null || path.isEmpty())
            return;

        try{
            File file = new File(path);
            file.delete();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static String saveToInternalStorage(Bitmap bitmapImage, String kind){

        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        // Create imageDir
        File mypath = null;
        try {
            mypath = File.createTempFile(kind, ".png", directory);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
                return mypath.getAbsolutePath();
            }
        }
        return mypath.getAbsolutePath();
    }

    public static ContextWrapper getCw() {
        return cw;
    }

    public static void setCw(ContextWrapper cw) {
        MediaHelper.cw = cw;
    }

    public static Bitmap getImageFromStorage(String path)
    {
        try {
            File f=new File(path);
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            return null;
        }

    }
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

}
