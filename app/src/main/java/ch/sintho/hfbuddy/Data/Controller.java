package ch.sintho.hfbuddy.Data;

import android.app.Activity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import ch.sintho.hfbuddy.Model.Mark;
import ch.sintho.hfbuddy.Model.PartialMarkType;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.R;

/**
 * Created by Sintho on 03.01.2016.
 */
public class Controller {

    private static Controller instance;

    public static Controller GetInstance()
    {
        if (instance == null)
            instance = new Controller();
        return instance;
    }

    public ArrayList<Subject> getSubjectsFromDb(Activity activity)
    {
        ArrayList<Subject> results = DbContext.getInstance(activity).executeQuery("SELECT * FROM " + DbContext.TABLE_SUBJECTS, Subject.class);
        return results;
    }

    public ArrayList<Mark> getMarksFromDb(Activity activity, int subjectid)
    {
        ArrayList<Mark> results = DbContext.getInstance(activity).executeQuery("SELECT * FROM " + DbContext.TABLE_MARKS + " WHERE " + DbContext.COLUMN_SUBJECT_FK + " = " + subjectid, Mark.class);
        return results;
    }

    public void FillFaecher(Activity activity, View view)
    {
        Spinner spinner = (Spinner) view;
        ArrayAdapter<Subject> adapter = new ArrayAdapter<>(activity, R.layout.spinner_layout);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        List<Subject> objs = DbContext.getInstance(activity).executeQuery("SELECT * FROM " + DbContext.TABLE_SUBJECTS, Subject.class);

        if (objs != null)
        {
            for (Subject sub : objs) {
                adapter.add(sub);
            }
        }

        spinner.setAdapter(adapter);
    }

    public void FillTeilnotenArten(Activity activity, View view)
    {
        AutoCompleteTextView autocompleteText = (AutoCompleteTextView) view;
        ArrayAdapter<PartialMarkType> adapter = new ArrayAdapter<>(activity,android.R.layout.simple_spinner_item);
        List<PartialMarkType> objs = DbContext.getInstance(activity).executeQuery("SELECT * FROM " + DbContext.TABLE_PARTIALMARK_TYPES, PartialMarkType.class);

        if (objs != null)
        {
            for (PartialMarkType type : objs) {
                adapter.add(type);
            }
        }

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        autocompleteText.setAdapter(adapter);
        autocompleteText.setThreshold(1);
    }
}
