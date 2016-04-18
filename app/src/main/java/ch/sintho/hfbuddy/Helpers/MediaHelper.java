package ch.sintho.hfbuddy.Helpers;

import android.content.Context;
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
import java.io.FileInputStream;

/**
 * Created by Sintho on 17.04.2016.
 */
public class MediaHelper {

    public static String GetAbsolutePath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >=
                Build.VERSION_CODES.KITKAT;
        Log.i("URI",uri+"");
        String result = uri+"";
        // DocumentProvider
        //  if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
        if (isKitKat && (result.contains("media.documents"))) {

            String[] ary = result.split("/");
            int length = ary.length;
            String imgary = ary[length-1];
            final String[] dat = imgary.split("%3A");

            final String docId = dat[1];
            final String type = dat[0];

            Uri contentUri = null;
            if ("image".equals(type)) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            } else if ("video".equals(type)) {

            } else if ("audio".equals(type)) {
            }

            final String selection = "_id=?";
            final String[] selectionArgs = new String[] {
                    dat[1]
            };

            return getDataColumn(context, contentUri, selection, selectionArgs);
        }
        else
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
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

    public static String GetSelectedImagePath(Context context, Uri uri)
    {
        String[] projection = {MediaStore.MediaColumns.DATA};

        CursorLoader cursorLoader = new CursorLoader(context, uri, projection, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }


    public static Bitmap GetOriginalPicture(Context context, Uri uri)
    {
        String selectedImagePath = MediaHelper.GetSelectedImagePath(context,uri);
        Bitmap bm = BitmapFactory.decodeFile(selectedImagePath);
        Bitmap original = ExifUtil.rotateBitmap(selectedImagePath, bm);
        bm.recycle();
        return original;
    }

    public static Bitmap GetThumbnailFromPicture(Bitmap bitmap, String filename, int size)
    {
        byte[] imageData = null;

        try
        {
            Bitmap bm;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            int scale = 1;
            while (options.outWidth / scale / 2 >= size
                    && options.outHeight / scale / 2 >= size)
                scale *= 2;

            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;

            bm = BitmapFactory.decodeFile(filename, options);
            return bm;

        }
        catch(Exception ex) {

        }
        return null;
    }
}
