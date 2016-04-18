package ch.sintho.hfbuddy.View.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import ch.sintho.hfbuddy.Activities.Main;
import ch.sintho.hfbuddy.Data.Controller;
import ch.sintho.hfbuddy.Helpers.ExifUtil;
import ch.sintho.hfbuddy.Helpers.MediaHelper;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.Model.Task;
import ch.sintho.hfbuddy.R;
import ch.sintho.hfbuddy.Security.PermissionManager;

/**
 * Created by Sintho on 09.01.2016.
 */
public class NewTaskFragment extends Fragment {

    static int mYear;
    static int mMonth;
    static int mDay;
    static EditText editDate;
    static Context context;
    static ImageView imageButton;
    private Animator mCurrentAnimator;
    private int mShortAnimationDuration;
    private Bitmap chosenBitmapOrignal;
    private Uri mImageUri;
    private RelativeLayout relativeLayout;

    public NewTaskFragment()
    {
        Calendar c=Calendar.getInstance();
        mYear=c.get(Calendar.YEAR);
        mMonth=c.get(Calendar.MONTH);
        mDay=c.get(Calendar.DAY_OF_MONTH);

    }
    protected static Dialog showDialog(int id) {
        switch (id) {
            case 1:
                return new DatePickerDialog(context,
                        mDateSetListener,
                        mYear, mMonth, mDay);

        }

        return null;

    }
    private static DatePickerDialog.OnDateSetListener mDateSetListener =new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            editDate.setText(new StringBuilder().append(mDay).append(".").append(mMonth+1).append(".").append(mYear));
        }

    };


    public static void selectSpinnerItemByValue(Spinner spnr, int id)
    {
        ArrayAdapter<Subject> adapter = (ArrayAdapter<Subject>) spnr.getAdapter();
        for (int position = 0; position < adapter.getCount(); position++)
        {
            if(adapter.getItemId(position) == id-1)
            {
                spnr.setSelection(position);
                return;
            }
        }
    }

    private void selectImage() {
        final CharSequence[] items = { "Aufnahme", "Gallerie" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getContext());
        builder.setTitle("Bild hinzufügen");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Aufnahme")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File photo;
                    try
                    {
                        // place where to store camera taken picture
                        photo = createTemporaryFile("picture", ".jpg");
                        photo.delete();
                    }
                    catch(Exception e)
                    {
                        Toast.makeText(getActivity(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_LONG);
                        return;
                    }
                    mImageUri = Uri.fromFile(photo);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                    startActivityForResult(intent, Main.CAMERA_REQUEST);
                } else if (items[item].equals("Gallerie")) {
                    PermissionManager.verifyStoragePermissions(getActivity());
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Wähle das Bild"),
                            Main.SELECT_FILE);
                }
            }
        });
        builder.show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View v = inflater.inflate(R.layout.activity_newtask,container,false);
        Controller.GetInstance().FillFaecher(this.getActivity(), v.findViewById(R.id.spinnernewTaskFach));

        final Button btn = (Button) v.findViewById(R.id.btnSaveTask);
        final EditText txtTitel = (EditText) v.findViewById(R.id.txtNewTaskTitle);
        final EditText txtNote = (EditText) v.findViewById(R.id.txtnewTaskBemerkung);
        final Spinner spinnerFach = (Spinner) v.findViewById(R.id.spinnernewTaskFach);
        relativeLayout = (RelativeLayout) v.findViewById(R.id.container);
        final Button btnDate = (Button) v.findViewById(R.id.btnnNewTaskDate);
        final Button btnAddPhoto = (Button) v.findViewById(R.id.btnNewTaskAddPhoto);
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                selectImage();
            }
        });

        imageButton = (ImageButton) v.findViewById(R.id.newTaskimagepreview);
        editDate = (EditText) v.findViewById(R.id.txtnewTaskDate);
        NewTaskFragment.context = v.getContext();

        //int subid = (int)getArguments().get("subjectid");
        //selectSpinnerItemByValue(spinnerFach, subid);

        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        editDate.setText(dateFormat.format(date));

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dialog dialog = NewTaskFragment.showDialog(1);
                dialog.show();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoomImageFromThumb(imageButton, chosenBitmapOrignal);
            }
        });

        mShortAnimationDuration = getResources().getInteger(
                android.R.integer.config_shortAnimTime);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Task task = new Task();
                task.setTitle(txtTitel.getText().toString());
                task.setNote(txtNote.getText().toString());

                DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

                try {
                    Date result = df.parse(editDate.getText().toString());
                    task.setDate(result);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Object obj = spinnerFach.getSelectedItem();
                if (obj != null) {
                    Subject subject = (Subject) obj;
                    task.setSubject(subject);
                }
                task.save();

                txtNote.setText("");
                txtTitel.setText("");
                DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                Date date = new Date();
                editDate.setText(dateFormat.format(date));

                Toast.makeText(v.getContext(), "Aufgabe erfolgreich gespeichert!", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }
    private File createTemporaryFile(String part, String ext) throws Exception
    {

        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);

        return File.createTempFile(part, ext, storageDir);
    }

    public Bitmap grabImage()
    {
        getActivity().getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr =getActivity().getContentResolver();
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            return bitmap;
        }
        catch (Exception e)
        {
            Toast.makeText(getActivity(), "Failed to load", Toast.LENGTH_SHORT).show();
        }
        return null;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == Main.CAMERA_REQUEST) {

                if (chosenBitmapOrignal != null)
                    chosenBitmapOrignal.recycle();

                Bitmap bitmap = grabImage();
                Bitmap thumbnail =Bitmap.createScaledBitmap(bitmap, imageButton.getWidth(), imageButton.getHeight(), false);
                String selectedImagePath = MediaHelper.GetAbsolutePath(this.getContext(),mImageUri);

                chosenBitmapOrignal = ExifUtil.rotateBitmap(selectedImagePath, bitmap);

                imageButton.setVisibility(View.VISIBLE);
                imageButton.setImageBitmap(thumbnail);

            } else if (requestCode == Main.SELECT_FILE) {

                if (chosenBitmapOrignal != null)
                    chosenBitmapOrignal.recycle();

                Uri selectedImageUri = data.getData();
                String selectedImagePath = MediaHelper.GetAbsolutePath(this.getContext(),selectedImageUri);

                Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);
                Bitmap thumbnail = resize(bitmap, imageButton.getWidth(), imageButton.getWidth());

                if (bitmap.getHeight() < 1000)
                    bitmap = resize(bitmap, relativeLayout.getWidth(), relativeLayout.getHeight());

                chosenBitmapOrignal = ExifUtil.rotateBitmap(selectedImagePath, bitmap);

                imageButton.setVisibility(View.VISIBLE);
                imageButton.setImageBitmap(thumbnail);
            }
        }
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }


    private void zoomImageFromThumb(final View thumbView, Bitmap bitmap) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (mCurrentAnimator != null) {
            mCurrentAnimator.cancel();
        }

        if (bitmap == null)
            return;

        // Load the high-resolution "zoomed-in" image.
        final ImageView expandedImageView = (ImageView) getActivity().findViewById(
                R.id.expanded_image);

        expandedImageView.setImageBitmap(bitmap);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        getActivity().findViewById(R.id.container)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        expandedImageView.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        expandedImageView.setPivotX(0f);
        expandedImageView.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(expandedImageView, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(expandedImageView, View.SCALE_X,
                        startScale, 1f)).with(ObjectAnimator.ofFloat(expandedImageView,
                View.SCALE_Y, startScale, 1f));
        set.setDuration(mShortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mCurrentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mCurrentAnimator = null;
            }
        });
        set.start();
        mCurrentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        expandedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(expandedImageView, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(expandedImageView,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(mShortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        expandedImageView.setVisibility(View.GONE);
                        mCurrentAnimator = null;
                    }
                });
                set.start();
                mCurrentAnimator = set;
            }
        });
    }
}
