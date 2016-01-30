package ch.sintho.hfbuddy.View.Fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ch.sintho.hfbuddy.Data.Controller;
import ch.sintho.hfbuddy.Data.DbContext;
import ch.sintho.hfbuddy.Model.Mark;
import ch.sintho.hfbuddy.Model.PartialMarkType;
import ch.sintho.hfbuddy.Model.Subject;
import ch.sintho.hfbuddy.R;
import ch.sintho.hfbuddy.View.Adapters.MarksViewAdapter;

/**
 * Created by Sintho on 09.01.2016.
 */
public class MarksContentFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private int mSubjectId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_marks, container, false);

        mRecyclerView = (RecyclerView) v.findViewById(R.id.marksrecyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(v.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        Bundle args = getArguments();

        mSubjectId = args.getInt("subjectid",0);
        mAdapter = new MarksViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        final TextView subject;
        TextView durschnitt;

        subject = (TextView) v.findViewById(R.id.txtfach);
        durschnitt = (TextView) v.findViewById(R.id.txtdurchschnitt);


        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabOnMarksView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewMarkFragment fragment = new NewMarkFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                Bundle bundle = new Bundle();
                bundle.putInt("subjectid", mSubjectId);
                fragment.setArguments(bundle);
                fragmentTransaction.replace(R.id.frame,fragment).addToBackStack("MarksContentFragment");
                fragmentTransaction.commit();
            }
        });


        Subject sub = DbContext.getInstance().GetObjectById(DbContext.TABLE_SUBJECTS, mSubjectId, Subject.class);
        ArrayList<Mark> marks = DbContext.getInstance().executeQuery("SELECT * FROM " + DbContext.TABLE_MARKS + " WHERE " + DbContext.COLUMN_SUBJECT_FK + " = " + sub.getId(), Mark.class);

        double durchschnitt = 0.00;

        if (marks != null && marks.size() > 0)
        {
            HashMap<Integer, List<Double>> map = new HashMap<>();

            for (Mark mark:marks) {
                PartialMarkType type = mark.getPartialmarktype();

                int typeid = 0;
                if (type != null) {
                    typeid = type.getId();
                }

                if (map.containsKey(typeid)) {
                    List<Double> list = map.get(typeid);
                    list.add(mark.getMark()/1000);
                    map.put(typeid, list);
                } else {
                    List<Double> list = new ArrayList<>();
                    list.add(mark.getMark()/1000);
                    map.put(typeid, list);
                }
            }

            for (List<Double> lst: map.values()) {
                double internalds = 0.00;
                for (int i = 0; i < lst.size() ; i++) {
                    internalds += lst.get(i);
                }
                durchschnitt += (internalds / lst.size());
            }
            if (map.values().size() != 0) {
                durchschnitt = (durchschnitt / map.values().size());
            }
        }

        subject.setText(sub.getName().toUpperCase());
        durschnitt.setText("ø " + Mark.formatMark(durchschnitt));

        // Code to Add an item with default animation
        //((MarksViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MarksViewAdapter) mAdapter).deleteItem(index);

        ((MarksViewAdapter) mAdapter).setOnItemClickListener(new MarksViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(final int position, View v) {
                AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                        .setTitle("Note löschen")
                        .setMessage("Wollen Sie diese Note wirklich löschen?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ((MarksViewAdapter) mAdapter).deleteItem(position);
                                Snackbar.make(getView(), "Note erfolgreich gelöscht!", Snackbar.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });

        return v;
    }

    private ArrayList<Mark> getDataSet() {
        return Controller.GetInstance().getMarksFromDb(getActivity(), mSubjectId);
    }
}
