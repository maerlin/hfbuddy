package ch.sintho.hfbuddy.View.Fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import ch.sintho.hfbuddy.Data.Controller;
import ch.sintho.hfbuddy.Data.DbContext;
import ch.sintho.hfbuddy.Model.Mark;
import ch.sintho.hfbuddy.Model.PartialMarkType;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.Model.Task;
import ch.sintho.hfbuddy.R;

/**
 * Created by Sintho on 09.01.2016.
 */
public class NewTaskFragment extends Fragment {

    static int mYear;
    static int mMonth;
    static int mDay;
    static EditText editDate;
    static Context context;

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_newtask,container,false);
            Controller.GetInstance().FillFaecher(this.getActivity(), v.findViewById(R.id.spinnernewTaskFach));

            final Button btn = (Button) v.findViewById(R.id.btnSaveTask);
            final EditText txtTitel = (EditText) v.findViewById(R.id.txtNewTaskTitle);
            final EditText txtNote = (EditText) v.findViewById(R.id.txtnewTaskBemerkung);
            final Spinner spinnerFach = (Spinner) v.findViewById(R.id.spinnernewTaskFach);

            final Button btnDate = (Button) v.findViewById(R.id.btnnNewTaskDate);
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
}
