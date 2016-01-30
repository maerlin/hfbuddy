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
import ch.sintho.hfbuddy.R;

/**
 * Created by Sintho on 09.01.2016.
 */
public class NewMarkFragment extends Fragment {

    static int mYear;
    static int mMonth;
    static int mDay;
    static EditText editDate;
    static Context context;

    public NewMarkFragment()
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
        final View v = inflater.inflate(R.layout.content_main,container,false);
            Controller.GetInstance().FillFaecher(this.getActivity(), v.findViewById(R.id.spinnerFach));
            Controller.GetInstance().FillTeilnotenArten(this.getActivity(), v.findViewById(R.id.suggestBoxArten));

            final Switch sw = (Switch) v.findViewById(R.id.switchTeilnoten);
            final Button btn = (Button) v.findViewById(R.id.btnSaveMark);
            final Switch switchletztenote = (Switch) v.findViewById(R.id.switchletzteTeilnote);
            final AutoCompleteTextView completebox = (AutoCompleteTextView) v.findViewById(R.id.suggestBoxArten);
            final TextView labelBereich = (TextView) v.findViewById(R.id.labelBereich);
            final EditText txtNote = (EditText) v.findViewById(R.id.txtMark);
            final Spinner spinnerFach = (Spinner) v.findViewById(R.id.spinnerFach);

            final Button btnDate = (Button) v.findViewById(R.id.btnDate);
            editDate = (EditText) v.findViewById(R.id.editDate);
            NewMarkFragment.context = v.getContext();

            int subid = (int)getArguments().get("subjectid");
            selectSpinnerItemByValue(spinnerFach, subid);

            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            Date date = new Date();
            editDate.setText(dateFormat.format(date));

            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Dialog dialog = NewMarkFragment.showDialog(1);
                    dialog.show();
                }
            });

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Mark mark = new Mark();
                    mark.setLastPartialMark(switchletztenote.isChecked());
                    mark.setMark(Double.parseDouble(txtNote.getText().toString()));

                    DateFormat df = new SimpleDateFormat("dd.MM.yyyy");

                    try {
                        java.util.Date result = df.parse(editDate.getText().toString());
                        mark.setDate(result);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    String stype = completebox.getText().toString().trim();

                    if (!Objects.equals(stype, "")) {
                        ArrayList<PartialMarkType> objects = DbContext.getInstance().executeQuery("SELECT * FROM " + DbContext.TABLE_PARTIALMARK_TYPES + " WHERE " + DbContext.COLUMN_NAME + " = '" + stype+"'", PartialMarkType.class);

                        PartialMarkType type;

                        if (objects == null || objects.isEmpty()) {
                            type = new PartialMarkType();
                            type.setName(completebox.getText().toString());
                            type.save();
                        } else {
                            type = objects.get(0);
                        }

                        mark.setPartialmarktype(type);
                    }

                    Subject subject = (Subject) spinnerFach.getSelectedItem();
                    mark.setSubject(subject);
                    mark.save();

                    switchletztenote.setChecked(false);
                    sw.setChecked(false);
                    completebox.setText("");
                    txtNote.setText("");
                    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                    Date date = new Date();
                    editDate.setText(dateFormat.format(date));

                    Toast.makeText(v.getContext(), "Note erfolgreich gespeichert!", Toast.LENGTH_SHORT).show();
                }
            });
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {


                labelBereich.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                switchletztenote.setVisibility(isChecked ? View.VISIBLE : View.GONE);
                completebox.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            }
        });

        return v;
    }
}
