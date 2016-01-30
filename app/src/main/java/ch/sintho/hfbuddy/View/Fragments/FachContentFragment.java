package ch.sintho.hfbuddy.View.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Objects;

import ch.sintho.hfbuddy.Data.Controller;
import ch.sintho.hfbuddy.Data.DbContext;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.R;

/**
 * Created by Sintho on 09.01.2016.
 */
public class FachContentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_subjects,container,false);
        Button button = (Button) v.findViewById(R.id.btnSpeichern);
        final EditText txtfach = (EditText) v.findViewById(R.id.txtFach);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String fachname = txtfach.getText().toString();

                if (!Objects.equals(fachname, ""))
                {
                    ArrayList<Subject> objects =  DbContext.getInstance().executeQuery("SELECT * FROM " + DbContext.TABLE_SUBJECTS + " WHERE " + DbContext.COLUMN_NAME + " = '" + fachname+"'", Subject.class);

                    if (objects == null || objects.isEmpty()) {
                        Subject subject = new Subject();
                        subject.setName(fachname);
                        subject.save();
                        txtfach.setText("");
                        Toast.makeText(v.getContext(), "Fach erfolgreich gespeichert", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        Toast.makeText(v.getContext(), "Das Fach " + fachname + " existiert bereits!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return v;
    }
}
